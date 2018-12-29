package chat3j.server.tasks;

import chat3j.messages.LeaveTopicMsg;
import chat3j.messages.TopicCreationMsg;
import chat3j.server.Chat3JMaster;
import com.esotericsoftware.kryonet.Connection;

/**
 * 토픽을 떠날때 처리하는 메시지
 */
public class LeaveTopicTask extends Task {

    public LeaveTopicTask(Connection conn, LeaveTopicMsg msg) {
        super(conn, msg);
    }

    @Override
    public void process(Chat3JMaster master) {
        LeaveTopicMsg response = (LeaveTopicMsg) msg;

        if (response.close) {// 서버와 연결 종료
            for(int i=0;i<response.topics.length;i++) {
                master.leaveTopic(response.topics[i],conn);
            }
        }
        else { // 특정 토픽에서 나갈경우
            master.leaveTopic(response.topics[0],conn);
        }
        response.ok = true;

        conn.sendTCP(response);
    }
}
