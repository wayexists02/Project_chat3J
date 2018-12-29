package chat3j;

import chat3j.server.Chat3JMaster;

/**
 * 외부로부터 (안드로이드 개발자 등이 직접 코드로 호출함) 시스템 이벤트를 받는 객체
 * 서버와 관련된 operation을 담당한다.
 */
public class MasterController {

    private Chat3JMaster master;

    public MasterController(Chat3JMaster master) {
        this.master = master;
    }

    public void open(int tcpPort, int udpPort) {
        master.setPort(tcpPort, udpPort);
        new Thread(() -> master.start()).start();
    }
    public void close() {

    }
}
