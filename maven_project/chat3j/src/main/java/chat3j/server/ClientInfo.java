package chat3j.server;

import com.esotericsoftware.kryonet.Connection;

/**
 * 토픽 내에 저장될 토픽 구성원 정보
 */
public class ClientInfo {

    public String address;
    public int tcp;
    public int udp;
    public Connection conn;
    public boolean equals(ClientInfo info) {
        return this.conn == info.conn;//서버와 connection은 항상 1개이다.
    }

}
