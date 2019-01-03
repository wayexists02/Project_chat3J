package chat3j.client;

import chat3j.client.commands.CommunicationInputCommand;
import chat3j.client.commands.DisconnectBetweenClientCommand;
import chat3j.client.commands.PublisherCommand;
import chat3j.client.data.Data;
import chat3j.messages.Message;
import chat3j.messages.TextDataMsg;
import chat3j.messages.VoiceDataMsg;
import chat3j.utils.Logger;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.*;

/**
 * 클라이언트는 여러개의 퍼블리셔를 가짐.
 * 클라이언트가 하나의 토픽에 입장하면 거기에 해당하는 퍼블리셔가 생성됨.
 */
public class Publisher {

    private final Logger logger = Logger.getLogger();

    private final List<ClientThread> subscribers; // 다른 클라이언트의 퍼블리셔 서버와 연결된 클라이언트들.
    private final Queue<PublisherCommand> commandQueue; // 작업 큐
    private Server server; // 이 퍼블리셔를 위한 서버. 다른 클라이언트에게 메시지를 보내기 위함.
    private int tcp;
    private int udp;

    private boolean ok; // 퍼블리셔의 메인 루프는 ok=true일때만 돈다.
    private boolean stop; // 퍼블리셔 정지 및 소멸.

    private Communication comm;

    private Thread mainThread;

    private int count = 0;

    public Publisher() {
        this.subscribers = new LinkedList<>();
        this.commandQueue = new PriorityQueue<>();
        this.server = new Server(65536, 65536);
        this.tcp = 0;
        this.udp = 0;
        this.comm = null;
        this.ok = true;
        this.stop = true; // 일단 true로
    }

    public void setCommunication(Communication comm) {
        if (this.comm != null)
            this.comm.interrupt();
        this.comm = comm;
        this.comm.start();
    }

    // 퍼블리셔를 시작함. 메인루프를 실행
    public void start() {
        server.start();

        // 메인 루프를 실행
        mainThread = new Thread(() -> {
            //System.out.println(Thread.currentThread().getName());
            run();
        });
        mainThread.start();

        // 커뮤니케이션 시작.
        //comm.start();

        logger.info("[PUBLISHER] Publisher started");
    }

    // 메인 루프
    public void run() {
        stop = false;

        // ok 인 동안 계속 실행
        while (ok) {
            int count = 3;

            // 작업 큐에 작업이 있으면 수행 없으면 패스.
            // 작업이 너무 많으면 3개만 수행.
            while (!commandQueue.isEmpty() && count > 0) {
                synchronized (commandQueue) {
                    PublisherCommand cmd = commandQueue.poll();
                    cmd.exec(this);
                }
                count -= 1;
            }

            // 다른 스레드가 원활하게 수행할 수 있게 쉰다.
            Thread.yield();
        }

        stop = true;
    }

    // 이 퍼블리셔가 있는 토픽 내의 모든 인원에게 메시지를 브로드캐스트
    public void broadcast(Message msg) {
        //logger.info("BROADCAST to " + subscribers.size() + " subscribers.");

        synchronized (Integer.class) {
            server.sendToAllUDP(msg);

            count += 1;

            if (count > 100) {
                count = 0;
                logger.info("BROADCAST to " + server.getConnections().length + " subscribers.");
            }
        }
    }

    public Communication.ECommunicationType getCommType() {
        if (comm.getClass() == VoiceCommunication.class)
            return Communication.ECommunicationType.VOICE;
        else if (comm.getClass() == ChatCommunication.class)
            return Communication.ECommunicationType.CHAT;
        else
            return null;
    }

    public void communicate(Data data) {
        /*
        synchronized (comm) {
            comm.writeData(data);
        }
        */

        if (comm != null) {
            //logger.info("Received");
            comm.writeData(data);
        }
    }

    public void close() {
        logger.info("CLOSE publisher");
        comm.interrupt();
        ok = false;
        destroy();
    }

    // 이 토픽에 다른 클라이언트가 들어오면 이 메소드를 통해 그 쿨라이언트와 통신연결
    public void connectTo(String addr, int tcp, int udp) {
        Client client = new Client(65536, 65536);

        try {
            ClientThread th = new ClientThread(client);
            new Thread(th).start();

            Message.registerMessageForPublisher(client);
            client.addListener(new ConnectionListener(this, false));
            //client.setKeepAliveUDP(2000);

            //logger.info("TCP: " + tcp + ", UDP: " + udp);
            client.connect(10000, addr, tcp, udp);

            synchronized (subscribers) {
                subscribers.add(th);
            }

            logger.info("[PUBLISHER] Connected to " + addr);
            logger.info("[PUBLISHER] Current number of client in topic: " + (subscribers.size() + 1));

        } catch (IOException exc) {
            exc.printStackTrace();
            client.stop();
            client.close();
        }
    }

    // 클라이언트가 이 토픽에서 나가면 마스터로부터 메시지가 올거다. 그럼 소켓들을 검사해서
    // 끊어진 소켓은 삭제.
    public void disconnect() {
        for (ClientThread th: subscribers) {
            if (!th.getClient().isConnected()) {
                th.close();

                synchronized (subscribers) {
                    subscribers.remove(th);
                }
            }
        }
    }

    // 퍼블러셔의 tcp 포트
    public int tcp() {
        return tcp;
    }

    //퍼블리셔의 udp 포트
    public int udp() {
        return udp;
    }

    //퍼블리셔 소멸
    public void destroy() {
        ok = false;

        while (!stop) Thread.yield();

        for (ClientThread clnt : subscribers) {
            clnt.close();
        }

        server.stop();
        server.close();
    }

    // 퍼블리셔의 포트를 할당함.
    public boolean assignPort() {
        boolean success = false;
        Random r = new Random(System.currentTimeMillis()); // 난수생성기

        long time1 = System.currentTimeMillis();

        server.addListener(new ConnectionListener(this, true));
        Message.registerMessageForPublisher(server);

        do {
            try { // 빈 포트를 찾는다.
                int port1 = r.nextInt(55536) + 10000;
                int port2 = r.nextInt(55536) + 10000;

                //일단 바인딩 해보고 성공하면 전역변수에 포트할당.
                this.server.bind(port1, port2);

                tcp = port1;
                udp = port2;
                success = true;
            } catch (IOException exc) {
            }

        } while (!success && (System.currentTimeMillis() - time1) < 10000); // 10초동안 빈 포트 못찾으면 리턴

        logger.info("[PUBLISHER] TCP: " + tcp);
        logger.info("[PUBLISHER] UDP: " + udp);

        return success;
    }

    public boolean getStop() {
        return stop;
    }

    // 퍼블리셔 내에서 생성되는 모든 소켓을 위한 리스너
    class ConnectionListener extends Listener {

        public final Publisher pub;
        public final boolean isServer; // 이 퍼블리셔 서버의 소켓인가, 다른 클라이언트의 퍼블리셔 서버로 연결된 소켓인가

        public ConnectionListener(Publisher pub, boolean isServer) {
            this.pub = pub;
            this.isServer = isServer;
        }

        @Override
        public void connected(Connection conn) { // 누가 나한테 연결했어

        }

        @Override
        public void disconnected(Connection conn) { // 누군가 연결을 끊었다.
            if (!isServer) { // 만약, 나의 client 객체들중 하나가 끊겼다면 검사해서 제거해야함.
                logger.info("[SUBSCRIBER] NODE disconnected.");
                DisconnectBetweenClientCommand cmd = new DisconnectBetweenClientCommand();

                synchronized (pub.commandQueue) {
                    pub.commandQueue.add(cmd);
                }
            }
            else { // 그러나 server 객체에서 끊긴 건 검사할 필요가 없음.
                logger.info("[PUBLISHER] NODE disconnected.");
            }
        }

        @Override
        public void received(Connection conn, Object obj) {
            if (obj instanceof VoiceDataMsg) {
                //logger.info("[PUBLISHER] VOICE received");
                VoiceDataMsg msg = (VoiceDataMsg) obj;

                CommunicationInputCommand cmd = new CommunicationInputCommand(conn, msg);

                synchronized (pub.commandQueue) {
                    pub.commandQueue.add(cmd);
                }
            }
            else if (obj instanceof TextDataMsg) {
                TextDataMsg msg = (TextDataMsg) obj;

                CommunicationInputCommand cmd = new CommunicationInputCommand(conn, msg);

                synchronized (pub.commandQueue) {
                    pub.commandQueue.add(cmd);
                }
            }
        }
    }

    public class ClientThread implements Runnable {

        private Client client;

        public ClientThread(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            client.start();
        }

        public void close() {
            client.stop();
            client.close();
        }

        public Client getClient() {
            return client;
        }
    }
}
