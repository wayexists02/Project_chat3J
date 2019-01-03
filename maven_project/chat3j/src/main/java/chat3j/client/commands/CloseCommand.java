package chat3j.client.commands;

import chat3j.client.Chat3JNode;
import chat3j.messages.LeaveTopicMsg;
import chat3j.messages.Message;
import com.esotericsoftware.kryonet.Connection;

/**
 * 노드를 닫거나 토픽 하나를 떠나는 명령
 */
public class CloseCommand extends PostCommand {

    public CloseCommand(Connection conn, Message msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Chat3JNode node) {
        LeaveTopicMsg response = (LeaveTopicMsg) msg;

        if (response.topics != null) { // 만약, 토픽 한개 이상에 가입되어 있을 경우
            for (String topic : response.topics) { // 모든 토픽에 해당하는 퍼블리셔를 닫는다.
                if (!node.actualLeaveTopic(topic)) {
                    node.logger.error("No given named topic '" + topic + "'.");
                    node.optionOk(response.optionId, false, "No given named topic + '" + topic + "'.");
                }
            }
        }

        if (response.close) { // 만약, 노드를 닫는 명령일 경우
            node.actualClose(); // 노드를 닫음
            node.optionOk(response.optionId, true, "Success.");
        }
    }
}
