package chat3j.server.tasks;

import chat3j.messages.EnterTopicMsg;
import chat3j.messages.ReadyForEnterMsg;
import chat3j.server.Chat3JMaster;
import chat3j.server.Topic;
import com.esotericsoftware.kryonet.Connection;

/**
 * 새로운 클라이언트가 토픽에 들어왔을때, 생성되는 명령.
 */
public class EnterTopicTask extends Task {

    public EnterTopicTask(Connection conn, ReadyForEnterMsg msg) {
        super(conn, msg);
    }

    @Override
    public void process(Chat3JMaster master) {
        ReadyForEnterMsg response = (ReadyForEnterMsg) msg;

        Topic topic = master.getTopic(response.topic);

        // 새로 들어온 클라이언트에 대한 정보 취득
        EnterTopicMsg newmsg = new EnterTopicMsg();
        newmsg.topic = topic.topic();
        newmsg.address = conn.getRemoteAddressUDP().getHostName();
        newmsg.tcp = response.tcp;
        newmsg.udp = response.udp;

        // 기존 토픽 인원들에게 브로드캐스트
        topic.broadcast(newmsg);
        //마지막으로 새로 온 놈을 토픽에 추가
        topic.addClient(conn, response.tcp, response.udp);
    }
}
