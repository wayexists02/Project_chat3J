package chat3j.server.tasks;

import chat3j.messages.DisconnectToServerMsg;
import chat3j.messages.LeaveTopicMsg;
import chat3j.messages.Message;
import chat3j.options.Option;
import chat3j.server.Chat3JMaster;
import com.esotericsoftware.kryonet.Connection;

public class  CloseServerTask extends Task {

    public  CloseServerTask(Connection conn, Message msg) {
        super(conn, msg);
    }

    @Override
    public void process(Chat3JMaster master) {

        master.get_topic_list().clear();//모든 토픽 삭제
        Connection[] conns = master.getConnections();
        DisconnectToServerMsg msg = new DisconnectToServerMsg();
        for(int i=0;i<conns.length;i++) {//현재 연결된 클라이언트에게 종료하라고 전달함
            conns[i].sendTCP(msg);
        }
        master.actualClose();
        super.process(master);
    }
}
