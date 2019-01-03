package com.chat3j.core.client.commands;

import com.esotericsoftware.kryonet.Connection;

import com.chat3j.core.client.Chat3JNode;
import com.chat3j.core.messages.LeaveTopicMsg;
import com.chat3j.core.messages.Message;

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
