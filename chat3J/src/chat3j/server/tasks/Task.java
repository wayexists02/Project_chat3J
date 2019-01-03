package chat3j.server.tasks;

import chat3j.messages.Message;
import chat3j.messages.UpdateTopicListMsg;
import chat3j.server.Chat3JMaster;
import com.esotericsoftware.kryonet.Connection;

import java.util.Iterator;

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

    public void process(Chat3JMaster master) {
        Connection[] connections = master.getConnections();
        UpdateTopicListMsg response = new UpdateTopicListMsg();
        //보낼 메시지 공간
        String[] names = new String[master.get_topic_list().keySet().size()];
        String[] types = new String[master.get_topic_list().keySet().size()];
        Iterator<String> _names = master.get_topic_list().keySet().iterator();//키값목록
        String cur_topic;
        int i=0;
        while(_names.hasNext()) {
            cur_topic = _names.next();
            names[i] = cur_topic;
            types[i] = master.get_topic_list().get(cur_topic).getCommunicationType();
            i++;
        }
        response.topics = names;
        response.types = types;

        for(int j=0; j<connections.length;j++) {// 모든 사용자에게 response object(토픽목록)를 보내준다.
            connections[j].sendTCP(response);
        }
    } // 명령을 수행하는 메소드
}
