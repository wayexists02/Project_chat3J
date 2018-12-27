package chat3j.server.tasks;

import chat3j.messages.TopicCreationMsg;
import chat3j.server.Chat3JMaster;
import com.esotericsoftware.kryonet.Connection;

/**
 * 토픽 생성 요청을 받았을 때 생성되는 명령
 */
public class CreateTopicTask extends Task {

    public CreateTopicTask(Connection conn, TopicCreationMsg msg) {
        super(conn, msg);
    }

    @Override
    public void process(Chat3JMaster master) {
        TopicCreationMsg response = (TopicCreationMsg) msg;

        // 토픽을 추가하려고 시도해봄
        if (master.addTopic(response.topic, conn, response.tcp, response.udp)) // 토픽이 성공적으로 추가됨
            response.success = true;
        else // 토픽이 이미 있어서 추가가 불가능
            response.success = false;

        conn.sendTCP(response);
    }
}
