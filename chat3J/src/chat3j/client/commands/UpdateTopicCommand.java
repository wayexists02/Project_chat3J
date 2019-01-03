package chat3j.client.commands;

import chat3j.client.Chat3JNode;
import chat3j.messages.UpdateTopicListMsg;
import com.esotericsoftware.kryonet.Connection;

public class UpdateTopicCommand extends PostCommand {

    public UpdateTopicCommand(Connection conn, UpdateTopicListMsg msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Chat3JNode node) {
        UpdateTopicListMsg response = (UpdateTopicListMsg) msg;
        node.updateTopicList(response.topics, response.types);
    }
}
