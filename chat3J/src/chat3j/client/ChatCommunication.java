package chat3j.client;

import chat3j.Chat3JSourceDevice;
import chat3j.Chat3JTargetDevice;
import chat3j.CommunicationData;
import chat3j.StringData;
import chat3j.client.data.Data;
import chat3j.client.data.TextData;
import chat3j.messages.TextDataMsg;
import chat3j.utils.Logger;

/**
 * 채팅을 위한 커뮤니케이션 객체
 */
public class ChatCommunication extends Communication {

    // 안드로이드 textarea 에 해당하는 어댑터 할당하면 될듯
    //private TextAreaAdapter adapter;

    /**
     * 이 생성자로 객체 생성해야 함.
     * @param pub       이 커뮤니케이션 객체의 주인인 퍼블리셔 객체
     * @param adapter   어댑터 객체
     */
    /*
    public ChatCommunication(Publisher pub, TextAreaAdapter adapter) {
        super(pub);
        this.adapter = adapter;
    }
    */

    /**
     * 이 생성자는 사용되면 안됨.
     */
    /*
    public ChatCommunication(Publisher pub) {
        super(pub);
    }
    */

    //수정
    public ChatCommunication(Publisher pub, Chat3JSourceDevice source, Chat3JTargetDevice target) {
        super(pub, source, target);
    }

    @Override
    public boolean writeData(Data data) {
        /*
        TextData tdata = (TextData) data;
        adapter.write(tdata.getData());
        return true;
        */

        //수정
        if (source  == null) {
            Logger.getLogger().error("Source device must be set");
            return false;
        }

        try {
            TextData tData = (TextData) data;
            CommunicationData cData = tData.getData();
            source.writeData(cData);
            return true;
        } catch (ClassCastException exc) {
            Logger.getLogger().error("Text communication can handle only TextData.");
            return false;
        }
    }

    @Override
    public Data readData() {
        /*
        TextData tdata = new TextData();
        tdata.setData(adapter.read());
        return tdata;
        */

        //수정
        if (target  == null) {
            Logger.getLogger().error("Target device must be set");
            return null;
        }

        try {
            StringData cData = (StringData) target.readData();
            TextData data = new TextData();
            data.setData(cData);
            return data;
        } catch (ClassCastException exc) {
            Logger.getLogger().error("Text data must be StringData.");
            return null;
        }
    }

    @Override
    public void start() {
        thread = new Thread(() -> {
            while (interrupt.ok) {
                TextData data = (TextData) readData();
                StringData cData = (StringData) data.getData();
                TextDataMsg msg = new TextDataMsg();
                msg.textData = cData.data;
                msg.type = "Chat";
                pub.broadcast(msg);
            }
        });

        thread.start();
    }
}
