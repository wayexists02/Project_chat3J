package chat3j;

import chat3j.client.Chat3JNode;
import chat3j.client.Communication;
import chat3j.options.Option;
import chat3j.utils.Logger;

import java.util.List;


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

    // 노드를 시작한다.
    public Option<Boolean> open() {
        if (!givenMasterInfo) {
            Logger.getLogger().error("You must give master information first!");
            Option<Boolean> opt = new Option<>();
            opt.ok = true;
            opt.data = false;
            opt.message = "Master information must be given first.";

            return opt;
        }

        return node.start();
    }

    public Option<List<String>> requestForTopicList() {
        return node.requestTopicList();
    }

    // 노드를 닫는다.
    public Option<Boolean> close() {
        return node.close();
    }

    // 토픽을 새로 생성한다.
    public Option<Boolean> createTopic(final String topic, final CommunicationType type) {
        if (type == CommunicationType.VOICE)
            return node.createTopic(topic, Communication.ECommunicationType.VOICE);
        else if (type == CommunicationType.CHAT)
            return node.createTopic(topic, Communication.ECommunicationType.CHAT);

        Option<Boolean> opt = new Option<>();
        opt.ok = true;
        opt.data = false;
        opt.message = "Invalid communication type.";

        return opt;
    }

    // 토픽에 입장한다.
    public Option<Boolean> enterTopic(final String topic) { return node.enterTopic(topic); }

    // 토픽을 떠난다.
    public Option<Boolean> exitFromTopic(final String topic) {
        return node.leaveFromTopic(topic);
    }

    // 마스터의 주소, 포트번호를 설정한다.
    public Option<Boolean> setMasterInformation(final String ipAddr, final int tcpPort, final int udpPort) {
        final Option<Boolean> opt = new Option<>();
        opt.data = false;

        new Thread(() -> {
            Option<Boolean> o1 = node.setMasterAddress(ipAddr);
            Option<Boolean> o2 = node.setMasterPort(tcpPort, udpPort);

            o1.waitFor();
            o2.waitFor();

            opt.data = o1.data && o2.data;
            if (opt.data)
                opt.message = "Success";
            else
                opt.message = "This node is already connected to master.";
            opt.ok = true;
        }).start();

        opt.waitFor();
        givenMasterInfo = true;

        return opt;
    }

    public enum CommunicationType {
        VOICE, CHAT;
    }
}
