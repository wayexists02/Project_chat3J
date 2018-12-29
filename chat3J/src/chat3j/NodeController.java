package chat3j;

import chat3j.client.Chat3JNode;
import chat3j.utils.Logger;

/**
 * 외부로부터 (안드로이드 개발자 등이 직접 코드로 호출함) 시스템 이벤트를 받는 객체
 * 클라이언트와 관련된 operation을 담당한다.
 */
public class NodeController {

    private Chat3JNode node;
    private boolean givenMasterInfo;

    public NodeController(Chat3JNode node) {
        this.node = node;
        this.givenMasterInfo = false;
    }

    public boolean open() {
        if (!givenMasterInfo) {
            Logger.getLogger().error("You must give master information!");
            return false;
        }

        new Thread(() -> node.start()).start();
        return true;
    }
    public void close() {
        new Thread(() -> node.close()).start();
    }
    public void createTopic(final String topic) {
        new Thread(() -> node.createTopic(topic)).start();
    }
    public void enterTopic(final String topic) { new Thread(() -> node.enterTopic(topic)).start();}
    public void exitFromTopic(final String topic) {
        new Thread(() -> node.leaveFromTopic(topic)).start();
    }
    public void setMasterInformation(final String ipAddr, final int tcpPort, final int udpPort) {
        new Thread(() -> node.setMasterAddress(ipAddr)).start();
        new Thread(() -> node.setMasterPort(tcpPort, udpPort)).start();
        givenMasterInfo = true;
    }
}
