package chat3j.client;

import chat3j.client.adapters.TextAreaAdapter;
import chat3j.options.Option;
import chat3j.client.commands.*;
import chat3j.messages.*;
import chat3j.utils.Logger;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.*;

public class Chat3JNode {

    public Logger logger;

    private User user;

    // 모든 클라이언트 노드는 토픽을 생성하거나 토픽에 들어가면 그 토픽을 위한 퍼블리셔가 생김.
    // 하나의 토픽 당 1개의 퍼블리셔가 있고 퍼블리셔를 이용해서 토픽 내에서 다른 클라이언트와 통신
    private Map<String, Publisher> publishers;

    // option list. 어떤 연산이 모두 수행됬음을 알리기 위한 옵션 객체 배열
    private Map<Integer, Option> optionList;
    // 옵션 리스트의 키가 될 id.
    public int optionId;

    // 마스터와 직접 통신하는 소켓.
    private Client clientToMaster;

    // 마스터와 통신할 소켓 포트
    private int tcp = 10321;
    private int udp = 10322;

    // 마스터의 주소
    private String address;

    //메인 루프는 ok=true 이면 돈다.
    private boolean ok;

    // 작업 큐
    private Queue<Command> commandQueue;

    //메인 루프를 돌리는 스레드
    private Thread mainThread;

    public Chat3JNode(String name) {
        this.user = new User(name);
        this.publishers = new HashMap<>();

        this.optionList = new HashMap<>();
        this.optionId = 0;

        this.clientToMaster = new Client();
        this.logger = Logger.getLogger();

        this.commandQueue = new PriorityQueue<>();

        this.ok = true;
    }

    // 노드를 시작한다
    public Option<Boolean> start() {
        Option<Boolean> option = new Option<>();

        try { // 마스터와 통신할 클라이언트 소켓 세팅
            clientToMaster.start();
            logger.info("Node was started...");

            Message.registerMessage(clientToMaster);

            clientToMaster.connect(10000, address, tcp, udp);
            logger.info("Connected to master.");

            clientToMaster.addListener(new ReceiveListener(this));

            // 옵션 업데이트
            option.data = true;
            option.message = "Success.";

        } catch (IOException exc) {
            exc.printStackTrace();
            clientToMaster.close();

            option.data = false;
            option.message = "Cannot connect to master.";
        }

        option.ok = true;

        // 메인 루프를 다른 스레드에서 돌림
        mainThread = new Thread(() -> {
            //System.out.println(Thread.currentThread().getName());
            run();
        });
        mainThread.start();

        return option;
    }

    // 메인 루프
    public void run() {

        while (ok) {
            checkCommand(); // 작업이 작업 큐에 있으면 수행.
            Thread.yield(); // 다른 스레드에게 프로세서 넘김.
        }
    }

    // 새로운 사람이 토픽에 들어오면 그 사람의 정보를 마스터로부터 얻어서 연결함.
    public void connectTo(String topic, String addr, int tcp, int udp) {
        Publisher pub = publishers.get(topic);
        new Thread(() -> pub.connectTo(addr, tcp, udp)).start();
    }

    // 토픽을 최초로 생성함
    public Option<Boolean> createTopic(String topic, Communication.ECommunicationType type) {
        Option<Boolean> option = new Option<>();

        if (type == Communication.ECommunicationType.VOICE) {
            boolean voice = false;
            for (Publisher pub : publishers.values()) {
                if (pub.getCommType() == Communication.ECommunicationType.VOICE) {
                    voice = true;
                    break;
                }
            }

            if (voice) {
                logger.error("Only one voice topic can be approved.");
                option.ok = true;
                option.data = false;
                option.message = "Only one voice topic can be approved.";
                return option;
            }
        }

        Publisher pub = new Publisher(type);
        if (!pub.assignPort()) // 토픽에서 메시지를 보내기 위해 서버를 생성하고 포트를 할당
            logger.info("CANNOT assign port");

        // 만약, 중복된 토픽을 생성하는 것이라면, 에러.
        if (publishers.containsKey(topic)) {
            logger.error("ERROR: Topic '" + topic + "' already exists!");
            pub.destroy();

            option.ok = true;
            option.data = false;
            option.message = "The topic already exists.";
        } else { // 중복되지 않았으면, 토픽을 추가하고 마스터에게 토픽을 생성했다는 메시지 보냄.
            publishers.put(topic, pub);

            TopicCreationMsg msg = new TopicCreationMsg();
            msg.topic = topic;
            msg.tcp = pub.tcp();
            msg.udp = pub.udp();
            if (type == Communication.ECommunicationType.CHAT)
                msg.commType = "Chat";
            else if (type == Communication.ECommunicationType.VOICE)
                msg.commType = "Voice";
            else
                msg.commType = "Invalid";

            // 옵션을 keep 해둔다.
            int id = putOption(option);
            msg.optionId = id;

            clientToMaster.sendTCP(msg);
        }

        return option;
    }

    // 노드를 닫는다. 연결 모두종료
    public Option<Boolean> close() {
        Option<Boolean> option = new Option<>();

        // 종료 의사를 마스터에게 보냄
        LeaveTopicMsg msg = new LeaveTopicMsg();
        if (publishers.size() > 0)
            msg.topics = new String[publishers.size()];
        msg.close = true; // 아예 연결을 끊겠다는 의미
        int i = 0;

        // 모든 토픽에서 나가겠다는 의사 전송
        for (String topic : publishers.keySet()) {
            msg.topics[i] = topic;
            i += 1;
        }

        int id = putOption(option);
        msg.optionId = id;

        clientToMaster.sendTCP(msg);

        return option;
    }

    // 하나의 토픽에서 나간다. 해당 퍼블리셔만 종료
    public Option<Boolean> leaveFromTopic(String topic) {
        Option<Boolean> option = new Option<>();

        // 어떤 토픽에서 나가겠다는 메시지 전송
        LeaveTopicMsg msg = new LeaveTopicMsg();
        msg.topics = new String[1];
        msg.topics[0] = topic;
        msg.close = false;
        msg.optionId = putOption(option);
        clientToMaster.sendTCP(msg);


        return option;
    }

    // 마스터의 IP주소 입력받음 start 전에 실행되어야함
    public Option<Boolean> setMasterAddress(String addr) {
        Option<Boolean> option = new Option<>();

        if (clientToMaster.isConnected()) {
            option.ok = true;
            option.data = false;
            option.message = "This node is already connected to master.";
        } else {
            this.address = addr;
            option.ok = true;
            option.data = true;
            option.message = "Success.";
        }

        return option;
    }

    // 마스터의 포트번호 입력받음 start 전에 실행되어야함
    public Option<Boolean> setMasterPort(int tcp, int udp) {
        Option<Boolean> option = new Option<>();

        if (clientToMaster.isConnected()) {
            option.ok = true;
            option.data = false;
            option.message = "This node is already connected to master.";
        } else {
            this.tcp = tcp;
            this.udp = udp;
            option.ok = true;
            option.data = true;
            option.message = "Success.";
        }

        return option;
    }

    // 마스터에서 종료 처리를 모두 마치면, 비로소 노드를 최종적으로 종료함
    public void actualClose() {
        // 모든 퍼블리셔 종료
        for (Publisher pub : publishers.values()) {
            pub.close();
        }

        // 퍼블리셔 리스트 클리어
        publishers.clear();
        // 마스터와 연결 종료
        clientToMaster.stop();
        clientToMaster.close();

        ok = false;
    }

    // 마스터에서 토픽 exit 과정을 모두 마치면 비로소 해당 퍼블리셔 제거.
    public boolean actualLeaveTopic(String topic) {
        if (!publishers.containsKey(topic))
            return false;

        publishers.get(topic).close();
        publishers.remove(topic);

        return true;
    }

    // 새로운 토픽에 들어가기 위한 함수(추가된 부분)
    public Option<Boolean> enterTopic(String topic) {
        Option<Boolean> option = new Option<>();

        RequestTopicMsg msg = new RequestTopicMsg();
        msg.topic = topic;
        msg.optionId = putOption(option);

        clientToMaster.sendTCP(msg);


        return option;
    }

    public Option<List<String>> requestTopicList() {
        Option<List<String>> option = new Option<>();
        option.data = new LinkedList<>();

        RequestForTopicListMsg msg = new RequestForTopicListMsg();
        msg.optionId = putOption(option);
        clientToMaster.sendTCP(msg);

        return option;
    }

    // 만약, 다른 토픽에 들어가는 것이라면, 이 함수 호출
    // 해당 토픽의 다른 클라이언트와 통신을 위해 이 클라이언트도 이 토픽에 해당하는 퍼블리셔 생성
    public void addPublisher(String topic, String type, int optId) {
        // 퍼블리셔 생성
        Publisher pub;
        if (type.equals("Voice")) {
            boolean voice = false;
            for (Publisher p : publishers.values()) {
                if (p.getCommType() == Communication.ECommunicationType.VOICE) {
                    voice = true;
                    break;
                }
            }
            if (voice) {
                logger.error("Only one voice topic can be approved.");

                Option<Boolean> opt = optionList.get(optId);
                opt.ok = true;
                opt.data = false;
                opt.message = "Only one voice topic can be approved.";
                return;
            }

            pub = new Publisher(Communication.ECommunicationType.VOICE);
        } else if (type.equals("Chat")) {
            pub = new Publisher(Communication.ECommunicationType.CHAT);
        } else {
            logger.error("Invalid communication type.");
            return;
        }

        pub.assignPort();
        publishers.put(topic, pub);
        pub.start();

        // 마스터에게 나도 퍼블리셔가 준비되었으니 해당 퍼블리셔의 다른 노드들과의 연결 중계 요청
        ReadyForEnterMsg msg = new ReadyForEnterMsg();
        msg.topic = topic;
        msg.tcp = pub.tcp();
        msg.udp = pub.udp();

        clientToMaster.sendTCP(msg);
    }

    // 마스터로부터 해당 이름을 가진 토픽을 최초로 생성했다는 승인을 받았는지 검사.
    public boolean approveTopic(String topic, boolean approved) {
        Publisher pub = publishers.getOrDefault(topic, null);

        if (!approved) { // 승인을 받지 못했으면
            if (pub != null) { // 만들어놓은 퍼블리셔 소멸시킴.
                pub.destroy();
                publishers.remove(topic);
            }

            return false;
        } else { // 승인을 받았으면 퍼블리셔를 작동시킨다
            pub.start();
            return true;
        }
    }

    // 토픽에 대한 퍼블리셔의 상태를 체크함
    public boolean checkPublisher(String topic) {
        if (!publishers.containsKey(topic)) {
            return false;
        }
        Publisher publisher = publishers.get(topic);
        return !publisher.getStop();
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

    // 연산을 마쳤다는 것을 알림 옵션 객체 업데이트
    public <T> void optionOk(int id, T data, String msg) {
        Option<T> option = optionList.get(id);
        option.ok = true;
        option.data = data;
        option.message = msg;
    }

    // 옵션을 전역변수 맵에 저장함. 옵션은 나중에 업데이트 하기 위해 저장함.
    private <T> int putOption(Option<T> opt) {
        int id;
        synchronized (this) {
            // option id는 스레드 안전하게 변경
            if (optionId == Integer.MAX_VALUE)
                optionId = Integer.MIN_VALUE;
            else
                optionId += 1;
        }

        if (!optionList.containsKey(optionId))
            optionList.put(optionId, opt);

        id = optionId;

        return id;
    }

    public int getSizeSubscribers(String topic) {
        return publishers.get(topic).getSizeSubscribers();
    }

    public void setTextAreaAdapter(String topic, TextAreaAdapter adapter) {
        Publisher pub = publishers.get(topic);
        pub.setTextAreaAdapter(adapter);
    }

    public TextAreaAdapter getTextAreaAdapter(String topic) {
        Publisher pub = publishers.get(topic);
        return pub.getTextAreaAdater();
    }

    public Communication.ECommunicationType getCommunicationType(String topic) {
        Publisher pub = publishers.get(topic);
        return pub.getCommType();
    }

    // 클라이언트 내에서 생성되는 connection을 위한 리스너
    class ReceiveListener extends Listener {

        private Chat3JNode node;

        public ReceiveListener(Chat3JNode node) {
            this.node = node;
        }

        @Override
        public void received(Connection conn, Object obj) { // 마스터로부터 메시지 수신
            if (obj instanceof FrameworkMessage) return;//tcp통신 유지를 위해 keepalive메시지를 계속 교환
            node.logger.info("");
            node.logger.info("----- New Message -----");

            if (obj instanceof TopicCreationMsg) { // 마스터로부터 토픽 최초 생성에 대한 답신을 받은 경우
                TopicCreationMsg msg = (TopicCreationMsg) obj;
                node.logger.info("RE: Topic creation");
                node.logger.info("Success: " + msg.success);

                // 작업 큐에 작업을 생성.
                CreateTopicCommand cmd = new CreateTopicCommand(conn, msg);
                node.commandQueue.add(cmd);
            } else if (obj instanceof RequestTopicMsg) { // 토픽에 입장하겠다는 것에 대한 답신을 받음.
                RequestTopicMsg msg = (RequestTopicMsg) obj;
                node.logger.info("RE: Request connection");
                node.logger.info("Found: " + msg.found);

                // 작업 큐에 작업을 생성.
                RequestTopicCommand cmd = new RequestTopicCommand(conn, msg);
                node.commandQueue.add(cmd);
            } else if (obj instanceof EnterTopicMsg) { // 다른 클라이언트가 토픽에 입장했다는 메시지를 받음.
                EnterTopicMsg msg = (EnterTopicMsg) obj;
                node.logger.info("RE: new client entered");

                // 작업 큐에 작업을 생성.
                ConnectToNewCommand cmd = new ConnectToNewCommand(conn, msg);
                node.commandQueue.add(cmd);
            } else if (obj instanceof LeaveTopicMsg) { // 토픽떠나겠다고 마스터에게 통보한 후 답신받음
                LeaveTopicMsg msg = (LeaveTopicMsg) obj;
                node.logger.info("RE: Close operation");

                CloseCommand cmd = new CloseCommand(conn, msg);
                node.commandQueue.add(cmd);
            } else if (obj instanceof RequestForTopicListMsg) { // 토픽 리스트 요청에 대한 답신받음
                RequestForTopicListMsg msg = (RequestForTopicListMsg) obj;
                node.logger.info("RE: Request for topic list");

                TopicListCommand cmd = new TopicListCommand(conn, msg);
                node.commandQueue.add(cmd);
            } else if (obj instanceof DisconnectToServerMsg) {// 서버가 종료 되었을시 클라이언트는 종료된다.
                DisconnectToServerMsg msg = (DisconnectToServerMsg) obj;

                node.logger.info("RE: Disconnected from server");
                actualClose();
            }

            node.logger.info("-----------------------");
            node.logger.info("");
        }
    }
}
