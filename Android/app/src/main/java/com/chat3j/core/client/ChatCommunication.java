package com.chat3j.core.client;

import com.chat3j.core.client.adapters.TextAreaAdapter;
import com.chat3j.core.client.data.Data;
import com.chat3j.core.client.data.TextData;
import com.chat3j.core.messages.TextDataMsg;

/**
 * 채팅을 위한 커뮤니케이션 객체
 */
public class ChatCommunication extends Communication {

    // 안드로이드 textarea 에 해당하는 어댑터 할당하면 될듯
    private TextAreaAdapter adapter;

    /**
     * 이 생성자로 객체 생성해야 함.
     * @param pub       이 커뮤니케이션 객체의 주인인 퍼블리셔 객체
     * @param adapter   어댑터 객체
     */
    public ChatCommunication(Publisher pub, TextAreaAdapter adapter) {
        super(pub);
        this.adapter = adapter;
    }

    /**
     * 이 생성자는 사용되면 안됨.
     * @param pub
     */
    public ChatCommunication(Publisher pub) {
        super(pub);
    }

    @Override
    public void start() {
        thread = new Thread(() -> {
            while (interrupt.ok) { // 인터럽트가 걸리면 더이상 돌지않음
                if(adapter == null)
                    continue;
                if (!adapter.isEmpty())
                    sendData();
            }
        });
        thread.start();
    }

    public boolean sendData(){
        Data data = readData();
        System.out.println("SendData : "+(String)data.getData());
        TextDataMsg msg = new TextDataMsg();
        msg.textData = (String) data.getData();
        System.out.println("Broadcast");
        getPub().broadcast(msg);
        return true;
    }

    @Override
    public boolean writeData(Data data) {
        TextData tdata = (TextData) data;
        adapter.write(tdata.getData());
        return true;
    }

    @Override
    public Data readData() {
        TextData tdata = new TextData();
        tdata.setData(adapter.read());
        return tdata;
    }

    public void setAdapter(TextAreaAdapter adapter) {
        this.adapter = adapter;
    }

    public TextAreaAdapter getAdapter() {
        return adapter;
    }
}
