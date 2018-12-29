package chat3j.client.commands;

import chat3j.client.Chat3JNode;
import chat3j.messages.RequestTopicMsg;
import com.esotericsoftware.kryonet.Connection;

/**
 * 마스터로부터 토픽 입장에 대한 답신을 받은 후 생성되는 명령
 */
public class RequestTopicCommand extends PostCommand {

    public RequestTopicCommand(Connection conn, RequestTopicMsg msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Chat3JNode node) {
        RequestTopicMsg response = (RequestTopicMsg) msg;

        if (response.found) { // 토픽에 입장 가능하다는 답신일 경우
            // 새로운 클라이언트에 새로운 토픽을 위한 퍼블리셔를 추가해줌
            node.addPublisher(response.topic);

            // 새로운 클라이언트의 퍼블리셔는 기존 인원에 대한 정보를 이용해 기존 인원들과 연결
            for (int i = 0; i < response.address.length; i++)
                node.connectTo(response.topic, response.address[i], response.tcp[i], response.udp[i]);

            node.optionOk(response.optionId, true, "Success");
        }
        else { // 토픽에 입장할 수 없다는 답신일 경우
            node.logger.error("ERROR: CANNOT found topic!");
            node.optionOk(response.optionId, false, "Cannot found topic.");
        }
    }
}
