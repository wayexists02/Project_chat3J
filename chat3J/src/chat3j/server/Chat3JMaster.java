package chat3j.server;

import chat3j.messages.*;
import chat3j.server.tasks.*;
import chat3j.utils.Logger;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

import java.io.IOException;
import java.util.*;

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

    // 마스터 포트를 설정한다.
    public void setPort(int tcp, int udp) {
        this.tcp = tcp;
        this.udp = udp;
    }
    // 생성된 토픽 리스트 출력
    public void list_topic() {
        Topic cur_topic;
        Iterator<ClientInfo> cur_clients;
        //테스트용 코드
        Iterator<String> keys = topicList.keySet().iterator();
        logger.info("----Topic List----");
        while(keys.hasNext()) {
            String key = keys.next();
            cur_topic = topicList.get(key);
            cur_clients = cur_topic.getClientList().iterator();
            logger.info("Name : "+ key);
            logger.info("Member : ");
            while(cur_clients.hasNext()) {
                ClientInfo cur_client = cur_clients.next();
                logger.info("IPAddress : "+cur_client.address+", tcp : "+cur_client.tcp+", udp : "+cur_client.udp);
            }
        }
        logger.info("----------------");
    }
    public Map<String,Topic> get_topic_list() {
        return topicList;
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
                list_topic();//현제 생성된 토픽 리스트를 보여준다.
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
    public boolean leaveTopic(String topic, Connection conn) {//특정 연결객체를 가진 클라를 토픽에서 제가한다.(신규)
        if(topicList.containsKey(topic)) {//토픽에 들어간경우 해당토픽배열에서 삭제
            Topic cur_topic = topicList.get(topic);
            List<ClientInfo> clients = cur_topic.getClientList();


            ClientInfo cli_info = new ClientInfo();
            cli_info.conn = conn;
            for(int i=0;i<clients.size();i++) {
                if(cli_info.equals(clients.get(i))) {
                    clients.remove(i);
                }
            }
            if(clients.size()==0) { //모든 인원이 나갔을 경우 토픽을 해쉬맵에서 삭제한다.
                topicList.remove(topic);
            }
            return true;
        } else {//토픽에 가입하지 않았을경우 실패
            return false;
        }
    }
    public Connection[] getConnections() {
        return master.getConnections();
    }
    public void close() {//모든 클라이언트와의 연결종료및 서버를 종료함
        taskQueue.add(new CloseServerTask(null,null));
    }
    public void actualClose() {//마스터노드를 닫는다.
        master.stop();
        master.close();
        ok = !ok;
    }
    // 마스터의 리스너
    class ReceiveListener extends Listener {

        private Chat3JMaster master;

        public ReceiveListener(Chat3JMaster master) {
            this.master = master;
        }

        @Override
        public void received(Connection conn, Object obj) { // 마스터의 메시지 수신
            if(obj instanceof FrameworkMessage) return; //토픽관련 메시지 이외에 tcp통신을 유지하기 위한 keepalive메시지를 서버와 계속 교환
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
            else if (obj instanceof LeaveTopicMsg) {
                LeaveTopicMsg msg = (LeaveTopicMsg) obj;

                // 받은 메시지를 바탕으로 작업을 만들어서 큐에 넣어줌.
                LeaveTopicTask task = new LeaveTopicTask(conn,msg);
                master.taskQueue.add(task);
            }
            else if (obj instanceof RequestForTopicListMsg) {
                RequestForTopicListMsg msg = (RequestForTopicListMsg)obj;

                TopicListTask task = new TopicListTask(conn,msg);
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
