package chat3j.server.tasks;

import chat3j.messages.RequestForTopicListMsg;
import chat3j.messages.RequestTopicMsg;
import chat3j.server.Chat3JMaster;
import chat3j.server.ClientInfo;
import chat3j.server.Topic;
import com.esotericsoftware.kryonet.Connection;

import java.util.Iterator;
import java.util.Map;

/**
 * 현재 열려있는 토픽 목록을 주고싶을때 생성하는 명령
 */
public class TopicListTask extends Task {

    public TopicListTask(Connection conn, RequestForTopicListMsg msg) {
        super(conn, msg);
    }

    @Override
    public void process(Chat3JMaster master) {

        RequestForTopicListMsg response = (RequestForTopicListMsg) msg;
        //현제 토픽리스트를 가져옴
        Map<String,Topic> topics = master.get_topic_list();
        Iterator<String> _topics = topics.keySet().iterator();

        String[] topic_list = new String[topics.size()];
        int i=0;
        while(_topics.hasNext()) {
            topic_list[i++] = _topics.next();
        }
        response.topic = topic_list;

            // 토픽이 있다는 의미와 기존 인원들에 대한 정보를 보냄.
            conn.sendTCP(response);
        super.process(master);
    }

}
