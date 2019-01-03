package chat3j.client;

import chat3j.Chat3JSourceDevice;
import chat3j.Chat3JTargetDevice;
import chat3j.CommunicationData;
import chat3j.client.data.Data;

/**
 * 커뮤니케이션 객체. 이 객체를 바로 쓰면 안되고,
 * VoiceCommunication, ChatCommunication을 사용해야함.
 */
public abstract class Communication {

    public static ECommunicationType getType(String t) {
        if (t.equals("Voice"))
            return ECommunicationType.VOICE;
        else if (t.equals("Chat"))
            return ECommunicationType.CHAT;
        else
            return null;
    }

    protected Thread thread;              // 커뮤니케이션 스레드
    protected Publisher pub;              // 이 커뮤니케이션 객체를 생성하고 가지고 있는 퍼블리셔 객체
    protected final Interrupt interrupt;  // 커뮤니케이션 스레드를 안전하게 종료시킬 인터럽트 객체

    //수정
    protected boolean received;
    protected Chat3JSourceDevice source;
    protected Chat3JTargetDevice target;
    protected boolean echo;

    /**
     * 커뮤니케이션 객체 생성자
     */
    /*
    public Communication(Publisher pub) {
        this.pub = pub;
        this.interrupt = new Interrupt();
        this.interrupt.ok = true;
    }
    */

    //수정
    public Communication(Publisher pub, Chat3JSourceDevice source, Chat3JTargetDevice target) {
        this.pub = pub;
        this.received = false;
        this.source = source;
        this.target = target;
        this.interrupt = new Interrupt();
        this.echo = false;
        interrupt.ok = true;
    }

    /**
     * 커뮤니케이션 스레드 시작
     */
    /*
    public void start() {
        thread = new Thread(() -> {
            while (interrupt.ok) { // 인터럽트가 걸리면 더이상 돌지않음
                Data data = readData();

                if (pub.getCommType() == ECommunicationType.VOICE) { // 음성 메시지 통신
                    if (data.getData() == null || ((ByteArrayData) data.getData()).data.length <= 0) {
                        Thread.yield();
                    } else {
                        VoiceDataMsg msg = new VoiceDataMsg();
                        msg.data = ((ByteArrayData) data.getData()).data;
                        msg.size = ((ByteArrayData) data.getData()).data.length;
                        pub.broadcast(msg);
                    }
                }
                else if (pub.getCommType() == ECommunicationType.CHAT) { // 텍스트 메시지 통신
                    TextDataMsg msg = new TextDataMsg();
                    msg.textData = (String) data.getData();
                    pub.broadcast(msg);
                }
            }
        });

        thread.start();
    }
    */


    /**
     * 커뮤니케이션을 종료시키는 인터럽터 메소드
     */
    public void interrupt() {
        interrupt.ok = false;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public abstract void start();

    /**
     * 커뮤니케이션 스레드에서 이 메소드 호출
     * 데이터를 읽어들임
     * @return
     */
    public abstract Data readData();

    /**
     * 데이터를 알맞은 디바이스에 출력.
     * @param data  데이터
     * @return
     */
    public abstract boolean writeData(Data data);

    /**
     * 커뮤니케이션 타입 enumerator
     */
    public enum ECommunicationType {
        VOICE, CHAT;
    }

    /**
     * 커뮤니케이션 스레드의 인터럽트를 위한 객체
     */
    class Interrupt {
        public boolean ok;
    }
}
