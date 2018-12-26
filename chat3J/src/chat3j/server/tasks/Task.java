package chat3j.server.tasks;

import chat3j.messages.Message;
import chat3j.server.Chat3JMaster;
import com.esotericsoftware.kryonet.Connection;

/**
 * '명령/작업'을 객체화
 */
public abstract class Task implements Comparable<Task> {

    public Connection conn;
    public Message msg;

    public Task(Connection conn, Message msg) {
        this.conn = conn;
        this.msg = msg;
    }

    @Override
    public int compareTo(Task t) {
        return 0;
    }

    public abstract void process(Chat3JMaster master); // 명령을 수행하는 메소드
}
