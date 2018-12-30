package chat3j.client;

import chat3j.client.commands.CommunicationInputCommand;
import chat3j.client.commands.DisconnectBetweenClientCommand;
import chat3j.client.commands.PublisherCommand;
import chat3j.client.data.Data;
import chat3j.messages.Message;
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

    private Logger logger = Logger.getLogger();

    private List<Client> subscribers; // 다른 클라이언트의 퍼블리셔 서버와 연결된 클라이언트들.
    private Queue<PublisherCommand> commandQueue; // 작업 큐
    private Server server; // 이 퍼블리셔를 위한 서버. 다른 클라이언트에게 메시지를 보내기 위함.
    private int tcp;
    private int udp;

    private boolean ok; // 퍼블리셔의 메인 루프는 ok=true일때만 돈다.
    private boolean stop; // 퍼블리셔 정지 및 소멸.

    private Communication.ECommunicationType commType;
    private Communication comm;

    private Thread mainThread;

    public Publisher(Communication.ECommunicationType type) {
        this.subscribers = new LinkedList<>();
        this.commandQueue = new PriorityQueue<>();
        this.server = new Server();
        this.tcp = 0;
        this.udp = 0;
        this.ok = true;
        this.stop = true; // 일단 true로
        this.commType = type;

        switch (commType) {
            case VOICE:
                comm = new VoiceCommunication(this);
                break;
            case CHAT:
                break;
            default:
                break;
        }
    }

    // 퍼블리셔를 시작함. 메인루프를 실행
    public void start() {
        server.addListener(new ConnectionListener(this, true));
        Message.registerMessageForPublisher(server);
        server.start();

        // 메인 루프를 실행
        mainThread = new Thread(() -> {
            //System.out.println(Thread.currentThread().getName());
            run();
        });
        mainThread.start();

        // 커뮤니케이션 시작.
        comm.start();

        logger.info("[PUBLISHTER] Publisher started");
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
                PublisherCommand cmd = commandQueue.poll();
                cmd.exec(this);

                count -= 1;
            }

            // 다른 스레드가 원활하게 수행할 수 있게 쉰다.
            Thread.yield();
        }

        stop = true;
    }

    public void broadcast(Message msg) {
        for (Connection conn : subscribers) {
            conn.sendUDP(msg);
        }
    }

    public void communicate(Data data) {
        comm.writeData(data);
    }

    public void close() {
        destroy();
    }

    // 이 토픽에 다른 클라이언트가 들어오면 이 메소드를 통해 그 쿨라이언트와 통신연결
    public void connectTo(String addr, int tcp, int udp) {
        Client client = new Client();

        try {
            client.start();
            Message.registerMessage(client);
            client.addListener(new ConnectionListener(this, false));

            client.connect(10000, addr, tcp, udp);

            subscribers.add(client);

            logger.info("[PUBLISHER] Connected to " + addr);
            logger.info("[PUBLISHER] Current number of client in topic: " + (subscribers.size() + 1));

        } catch (IOException exc) {
            exc.printStackTrace();
            client.close();
        }
    }

    // 클라이언트가 이 토픽에서 나가면 마스터로부터 메시지가 올거다. 그럼 소켓들을 검사해서
    // 끊어진 소켓은 삭제.
    public void disconnect() {
        for (Client clnt : subscribers) {
            if (!clnt.isConnected()) {
                clnt.close();
                subscribers.remove(clnt);
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

        for (Client clnt : subscribers)
            clnt.close();

        server.stop();
        server.close();
    }

    // 퍼블리셔의 포트를 할당함.
    public boolean assignPort() {
        boolean success = false;
        Random r = new Random(System.currentTimeMillis()); // 난수생성기

        long time1 = System.currentTimeMillis();

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
                DisconnectBetweenClientCommand cmd = new DisconnectBetweenClientCommand();
                pub.commandQueue.add(cmd);
            } else { // 그러나 server 객체에서 끊긴 건 검사할 필요가 없음.

            }
        }

        @Override
        public void received(Connection conn, Object obj) {
            if (obj instanceof VoiceDataMsg) {
                VoiceDataMsg msg = (VoiceDataMsg) obj;

                CommunicationInputCommand cmd = new CommunicationInputCommand(conn, msg);
                pub.commandQueue.add(cmd);
            }
        }
    }
}
