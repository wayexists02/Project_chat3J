package chat3j.server;

import chat3j.messages.Message;
import chat3j.messages.ReadyForEnterMsg;
import chat3j.messages.RequestTopicMsg;
import chat3j.messages.TopicCreationMsg;
import chat3j.server.tasks.CreateTopicTask;
import chat3j.server.tasks.EnterTopicTask;
import chat3j.server.tasks.RequestTopicTask;
import chat3j.server.tasks.Task;
import chat3j.utils.Logger;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Chat3JMaster {

    private Server master;
    private Logger logger;

    private Map<String, Topic> topicList;
    private Queue<Task> taskQueue;
    private int tcp = 10321;
    private int udp = 10322;

    private boolean ok;
    private Thread mainThread;

    public Chat3JMaster() {
        this.master = new Server();
        this.logger = Logger.getLogger();
        this.topicList = new HashMap<>();
        this.taskQueue = new PriorityQueue<>();
        this.ok = true;
    }

    // 마스터 시작
    public void start() {
        try { // 마스터 소켓 세팅
            master.start();
            logger.info("Master was started...");

            Message.registerMessage(master);

            master.bind(tcp, udp);
            logger.info("binded to port of TCP: " + tcp + ", UDP: " + udp + ".");

            master.addListener(new ReceiveListener(this));

        } catch (IOException exc) {
            exc.printStackTrace();
            master.close();
        }

        // 마스터 메인 루프를 다른 스레드로
        mainThread = new Thread(() -> run());
        mainThread.start();
    }

    // 마스터 메인 루프
    public void run() {
        while (ok) {
            if (taskQueue.isEmpty()) // 들어와있는 명령이 없으면 쉰다.
                Thread.yield();

            else { // 명령이 있으면 그것을 수행.
                Task task = taskQueue.poll();
                task.process(this);
            }

            Thread.yield();
        }
    }

    // 해당하는 토픽이 있는지 확인 후 있으면 그 토픽 객체 리턴
    public Topic getTopic(String topic) {
        return topicList.getOrDefault(topic, null);
    }

    // 새로운 토픽을 추가 conn이 방장이됨.
    public boolean addTopic(String topic, Connection conn, int tcp, int udp) {
        if (topicList.containsKey(topic)) { // 토픽이 이미 있으면 생성실패
            return false;
        }
        else { // 토픽이 없으면 추가하고 토픽에 방장 추가
            Topic newtopic = new Topic(topic);
            newtopic.addClient(conn, tcp, udp);
            topicList.put(topic, newtopic);
            return true;
        }
    }

    // 마스터 포트변경 (미구현)
    public void setPort(int tcp, int udp) {
        this.tcp = tcp;
        this.udp = udp;
    }

    // 마스터의 리스너
    class ReceiveListener extends Listener {

        private Chat3JMaster master;

        public ReceiveListener(Chat3JMaster master) {
            this.master = master;
        }

        @Override
        public void received(Connection conn, Object obj) { // 마스터의 메시지 수신
            master.logger.info("");
            master.logger.info("----- New Message -----");
            master.logger.info("Sender: " + conn.getRemoteAddressTCP().getHostName());

            if (obj instanceof TopicCreationMsg) { // 토픽 생성 요청 받음
                TopicCreationMsg msg = (TopicCreationMsg) obj;
                master.logger.info("RE: Topic Creation");
                master.logger.info("Topic: " + msg.topic);

                // 받은 메시지를 바탕으로 작업을 만들어서 큐에 넣어줌.
                CreateTopicTask task = new CreateTopicTask(conn, msg);
                master.taskQueue.add(task);
            }
            else if (obj instanceof RequestTopicMsg) { // 토픽에 입장하겠다는 요청 받음
                RequestTopicMsg msg = (RequestTopicMsg) obj;
                master.logger.info("RE: Topic Request");
                master.logger.info("TOPIC: " + msg.topic);

                // 받은 메시지를 바탕으로 작업을 만들어서 큐에 넣어줌.
                RequestTopicTask task = new RequestTopicTask(conn, msg);
                master.taskQueue.add(task);
            }
            else if (obj instanceof ReadyForEnterMsg) { // 토픽에 입장할 준비를 마쳤다는 요청 받음.
                ReadyForEnterMsg msg = (ReadyForEnterMsg) obj;

                // 받은 메시지를 바탕으로 작업을 만들어서 큐에 넣어줌.
                EnterTopicTask task = new EnterTopicTask(conn, msg);
                master.taskQueue.add(task);
            }

            master.logger.info("-----------------------");
            master.logger.info("");
        }

        @Override
        public void connected(Connection conn) {
            master.logger.info("");
            master.logger.info("----- New Connection -----");
            master.logger.info("Client: " + conn.getRemoteAddressTCP().getHostName());
            master.logger.info("--------------------------");
            master.logger.info("");
        }
    }
}
