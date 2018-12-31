package chat3j.server.tasks;

import chat3j.messages.RequestTopicMsg;
import chat3j.server.Chat3JMaster;
import chat3j.server.ClientInfo;
import chat3j.server.Topic;
import com.esotericsoftware.kryonet.Connection;

/**
 * 토픽에 입장하고 싶다는 요청을 받았을 때 생성되는 명령
 */
public class RequestTopicTask extends Task {

    public RequestTopicTask(Connection conn, RequestTopicMsg msg) {
        super(conn, msg);
    }

    @Override
    public void process(Chat3JMaster master) {
        RequestTopicMsg response = (RequestTopicMsg) msg;

        // 해당 이름의 토픽을 얻어옴
        Topic topic = master.getTopic(response.topic);

        if (topic == null) { // 토픽이 없음. 입장불가.
            response.found = false;

            // 원하는 토픽이 있는지 답신 (없다 로 보냄)
            conn.sendTCP(response);
        }
        else { // 토픽이 있음. 입장가능
            response.found = true;

            // 토픽에 있던 기존 인원들에 대한 정보 취득.
            response.address = new String[topic.getClientList().size()];
            response.tcp = new int[topic.getClientList().size()];
            response.udp = new int[topic.getClientList().size()];
            response.commType = topic.getCommunicationType();

            // 토픽에 있던 기존 인원들에 대한 정보 취득.
            for (int i = 0; i < topic.getClientList().size(); i++) {
                ClientInfo info = topic.getClientList().get(i);

                response.address[i] = info.conn.getRemoteAddressTCP().getHostName();
                response.tcp[i] = info.tcp;
                response.udp[i] = info.udp;
            }

            // 토픽이 있다는 의미와 기존 인원들에 대한 정보를 보냄.
            conn.sendTCP(response);
        }
    }
}
