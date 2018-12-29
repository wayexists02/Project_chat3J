package chat3j.server;

import chat3j.messages.Message;
import chat3j.utils.Logger;
import com.esotericsoftware.kryonet.Connection;

import java.util.LinkedList;
import java.util.List;

/**
 * 토픽 정보.
 * 여기 안에 구성원들에 대한 정보가 저장됨.
 */
public class Topic {

    private String topic;
    private List<ClientInfo> clientList;
    private Logger logger;
    private String commType; // 보이스? 텍스트?

    public Topic(String topic, String commType) {
        this.topic = topic;
        this.commType = commType;
        this.clientList = new LinkedList<>();
        this.logger = Logger.getLogger();

        logger.info("[TOPIC] Topic '" + topic + "' was created");
    }

    // 토픽 이름을 반환
    public String topic() {
        return topic;
    }

    // 보이스 채팅방인지, 텍스트 채팅방인지 반환.
    public String getCommunicationType() {
        return commType;
    }

    // 토픽에 새로운 클라이언트를 추가함
    // conn은 마스터가 클라이언트에게 메시지를 보내는 용도
    // tcp, udp는 그 클라이언트의 퍼블리셔 서버의 포트번호
    public void addClient(Connection conn, int tcp, int udp) {
        ClientInfo info = new ClientInfo();
        info.address = conn.getRemoteAddressTCP().getHostName();
        info.conn = conn;
        info.tcp = tcp;
        info.udp = udp;

        clientList.add(info);
        logger.info("[TOPIC] New Connection was created");
    }

    // 이 토픽의 모든 구성원에게 해당 메시지 브로드캐스트
    public void broadcast(Message msg) {
        logger.info("[TOPIC] Broadcast in topic '" + topic + "'");
        for (ClientInfo info : clientList)
            info.conn.sendTCP(msg);
    }

    // 토픽의 구성원 리스트 반환
    public List<ClientInfo> getClientList() {
        return clientList;
    }
}
