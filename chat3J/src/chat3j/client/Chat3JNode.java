package chat3j.client;

import chat3j.client.commands.*;
import chat3j.messages.*;
import chat3j.utils.Logger;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Chat3JNode {

    public Logger logger;

    private User user;

    // 모든 클라이언트 노드는 토픽을 생성하거나 토픽에 들어가면 그 토픽을 위한 퍼블리셔가 생김.
    // 하나의 토픽 당 1개의 퍼블리셔가 있고 퍼블리셔를 이용해서 토픽 내에서 다른 클라이언트와 통신
    private Map<String, Publisher> publishers;

    private Client clientToMaster;

    // 마스터와 통신할 소켓 포트
    private int tcp = 10321;
    private int udp = 10322;

    //메인 루프는 ok=true 이면 돈다.
    private boolean ok;

    // 작업 큐
    private Queue<Command> commandQueue;

    //메인 루프를 돌리는 스레드
    private Thread mainThread;

    public Chat3JNode(String name) {
        this.user = new User(name);
        this.publishers = new HashMap<>();

        this.clientToMaster = new Client();
        this.logger = Logger.getLogger();

        this.commandQueue = new PriorityQueue<>();

        this.ok = true;
    }

    public void start() {
        try { // 마스터와 통신할 클라이언트 소켓 세팅
            clientToMaster.start();
            logger.info("Node was started...");

            Message.registerMessage(clientToMaster);

            clientToMaster.connect(10000, "127.0.0.1", tcp, udp);
            logger.info("Connected to master.");

            clientToMaster.addListener(new ReceiveListener(this));

        } catch (IOException exc) {
            exc.printStackTrace();
            clientToMaster.close();
        }

        // 메인 루프를 다른 스레드에서 돌림
        mainThread = new Thread(() -> run());
        mainThread.start();
    }

    // 메인 루프
    public void run() {

        /// DEBUG ///
        createTopic("A"); // 작동확인을 위해 추가함.
        /////////////

        while (ok) {
            checkCommand(); // 작업이 작업 큐에 있으면 수행.
            Thread.yield(); // 다른 스레드에게 프로세서 넘김.
        }
    }

    // 미구현. 외부 입력 받기 위한 메소드
    public void requestTopicCreation() {

    }
    // 미구현. 외부 입력 받기 위한 메소드
    public void requestClose() {

    }

    // 새로운 사람이 토픽에 들어오면 그 사람의 정보를 마스터로부터 얻어서 연결함.
    public void connectTo(String topic, String addr, int tcp, int udp) {
        Publisher pub = publishers.get(topic);
        pub.connectTo(addr, tcp, udp);
    }

    // 토픽을 최초로 생성함
    public void createTopic(String topic) {
        Publisher pub = new Publisher();
        pub.assignPort(); // 토픽에서 메시지를 보내기 위해 서버를 생성하고 포트를 할당

        // 만약, 중복된 토픽을 생성하는 것이라면, 에러.
        if (publishers.containsKey(topic)) {
            logger.error("ERROR: Topic '" + topic + "' already exists!");
            pub.destroy();
            return;
        }
        else { // 중복되지 않았으면, 토픽을 추가하고 마스터에게 토픽을 생성했다는 메시지 보냄.
            publishers.put(topic, pub);

            TopicCreationMsg msg = new TopicCreationMsg();
            msg.topic = topic;
            msg.tcp = pub.tcp();
            msg.udp = pub.udp();

            clientToMaster.sendTCP(msg);
        }

    }

    // 미구현. 닫기옵션
    public void close() {
        LeaveTopicMsg msg = new LeaveTopicMsg();
        msg.topics = new String[publishers.size()];
        int i = 0;

        for (String topic : publishers.keySet()) {
            msg.topics[i] = topic;
            i += 1;
        }

        clientToMaster.sendTCP(msg);
    }

    // 만약, 다른 토픽에 들어가는 것이라면, 이 함수 호출
    // 해당 토픽의 다른 클라이언트와 통신을 위해 이 클라이언트도 이 토픽에 해당하는 퍼블리셔 생성
    public void addPublisher(String topic) {
        Publisher pub = new Publisher();
        pub.assignPort();
        publishers.put(topic, pub);
        pub.start();

        ReadyForEnterMsg msg = new ReadyForEnterMsg();
        msg.topic = topic;
        msg.tcp = pub.tcp();
        msg.udp = pub.udp();

        clientToMaster.sendTCP(msg);
    }

    // 마스터로부터 해당 이름을 가진 토픽을 최초로 생성했다는 승인을 받았는지 검사.
    public void approveTopic(String topic, boolean approved) {
        Publisher pub = publishers.getOrDefault(topic, null);

        if (!approved) { // 승인을 받지 못했으면
            if (pub != null) { // 만들어놓은 퍼블리셔 소멸시킴.
                pub.destroy();
                publishers.remove(topic);
            }

            //debug 작동 확인을 위해 추가해놓은 것
            RequestTopicMsg msg = new RequestTopicMsg();
            msg.topic = "A";
            clientToMaster.sendTCP(msg);
        }
        else { // 승인을 받았으면 퍼블리셔를 작동시킨다
            pub.start();
        }
    }

    // 작업 큐에 명령이 들어와있는지 확인하고 있으면 수행.
    private void checkCommand() {
        int count = 3; // 명령이 너무 많은 경우, 이것만 할 순 없다. 3개까지만 수행하고 중단.

        while (!commandQueue.isEmpty() && count > 0) {
            Command command = commandQueue.poll();
            command.exec(this);
            count -= 1;
        }
    }

    // 클라이언트 내에서 생성되는 connection을 위한 리스너
    class ReceiveListener extends Listener {

        private Chat3JNode node;

        public ReceiveListener(Chat3JNode node) {
            this.node = node;
        }

        @Override
        public void received(Connection conn, Object obj) { // 마스터로부터 메시지 수신
            node.logger.info("");
            node.logger.info("----- New Message -----");

            if (obj instanceof TopicCreationMsg) { // 마스터로부터 토픽 최초 생성에 대한 답신을 받은 경우
                TopicCreationMsg msg = (TopicCreationMsg) obj;
                node.logger.info("RE: Topic creation");
                node.logger.info("Success: " + msg.success);

                // 작업 큐에 작업을 생성.
                CreateTopicCommand cmd = new CreateTopicCommand(conn, msg);
                node.commandQueue.add(cmd);
            }
            else if (obj instanceof RequestTopicMsg) { // 토픽에 입장하겠다는 것에 대한 답신을 받음.
                RequestTopicMsg msg = (RequestTopicMsg) obj;
                node.logger.info("RE: Request connection");
                node.logger.info("Found: " + msg.found);

                // 작업 큐에 작업을 생성.
                RequestTopicCommand cmd = new RequestTopicCommand(conn, msg);
                node.commandQueue.add(cmd);
            }
            else if (obj instanceof EnterTopicMsg) { // 다른 클라이언트가 토픽에 입장했다는 메시지를 받음.
                EnterTopicMsg msg = (EnterTopicMsg) obj;
                node.logger.info("RE: new client entered");

                // 작업 큐에 작업을 생성.
                ConnectToNewCommand cmd = new ConnectToNewCommand(conn, msg);
                node.commandQueue.add(cmd);
            }

            node.logger.info("-----------------------");
            node.logger.info("");
        }
    }
}
