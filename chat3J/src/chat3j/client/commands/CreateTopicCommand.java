package chat3j.client.commands;

import chat3j.client.Chat3JNode;
import chat3j.messages.Message;
import chat3j.messages.TopicCreationMsg;
import com.esotericsoftware.kryonet.Connection;

/**
 * 마스터로부터 토픽 생성에 대한 답신을 받았을때, 생성되는 명령
 */
public class CreateTopicCommand extends PostCommand {

    public CreateTopicCommand(Connection conn, Message msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Chat3JNode node) {
        TopicCreationMsg response = (TopicCreationMsg) msg;

        // 토픽 승인여부 확인 후 옵션 업데이트
        if (node.approveTopic(response.topic, response.success))
            node.optionOk(response.optionId, true, "Success.");
        else
            node.optionOk(response.optionId, false, "Failed.");
    }
}
