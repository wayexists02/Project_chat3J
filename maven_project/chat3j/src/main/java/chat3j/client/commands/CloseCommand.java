package chat3j.client.commands;

import chat3j.client.Chat3JNode;
import chat3j.messages.LeaveTopicMsg;
import chat3j.messages.Message;
import com.esotericsoftware.kryonet.Connection;

public class CloseCommand extends PostCommand {

    public CloseCommand(Connection conn, Message msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Chat3JNode node) {
        LeaveTopicMsg response = (LeaveTopicMsg) msg;

        if (response.topics != null) {
            for (String topic : response.topics) {
                if (!node.actualLeaveTopic(topic)) {
                    node.logger.error("No given named topic '" + topic + "'.");
                    node.optionOk(response.optionId, false, "No given named topic + '" + topic + "'.");
                }
            }
        }

        if (response.close) {
            node.actualClose();
            node.optionOk(response.optionId, true, "Success.");
        }
    }
}
