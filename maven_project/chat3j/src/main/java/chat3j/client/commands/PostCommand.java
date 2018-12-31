package chat3j.client.commands;

import chat3j.messages.Message;
import com.esotericsoftware.kryonet.Connection;

/**
 * 마스터와 클라이언트간 메시지로 인해 생성되는 명령
 */
public abstract class PostCommand extends Command {

    public Message msg;
    public Connection conn;

    public PostCommand(Connection conn, Message msg) {
        this.conn = conn;
        this.msg = msg;
    }
}
