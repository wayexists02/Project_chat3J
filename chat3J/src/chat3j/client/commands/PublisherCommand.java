package chat3j.client.commands;

import chat3j.client.Publisher;
import chat3j.messages.Message;
import com.esotericsoftware.kryonet.Connection;

/**
 * 클라이언트들 사이에 오가는 메시지로 발생하는 명령.
 */
public abstract class PublisherCommand implements Comparable<PublisherCommand> {

    protected Connection conn;
    protected Message msg;

    public PublisherCommand(Connection conn, Message msg) {
        this.conn = conn;
        this.msg = msg;
    }
    public PublisherCommand() {
        this.conn = null;
        this.msg = null;
    }

    public abstract void exec(Publisher pub);

    @Override
    public int compareTo(PublisherCommand cmd) {
        return 0;
    }
}
