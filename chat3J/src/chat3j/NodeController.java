package chat3j;

import chat3j.client.Chat3JNode;
import chat3j.client.Communication;
import chat3j.options.Option;
import chat3j.utils.Logger;

import java.util.Date;
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
    public Option<Boolean> enterTopic(final String topic) {
        return node.enterTopic(topic);
    }

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

    // Topic 으로 작업 처리를 확인 -> 30초동안 완료가 안됬으면 실패
    public boolean checkTopic(TaskObject taskObject) {
        boolean loop = true;
        long runningTime = new Date().getTime();
        while (loop) {
            if (taskObject.getTYPE() == Task_TYPE.CREATE || taskObject.getTYPE() == Task_TYPE.ENTER) {
                System.out.println("IN LOOP");
                if (node.checkPublisher(taskObject.getTOPIC()))
                    return true;
            } else if (taskObject.getTYPE() == Task_TYPE.LEAVE) {
                if (!node.checkPublisher(taskObject.getTOPIC()))
                    return true;
            } else {
                return false;
            }
            // 30초동안 loop를 돌면 종료
            if (new Date().getTime() >= runningTime + 1000 * 30)
                loop = false;
        }
        return false;
    }

    public enum CommunicationType {
        VOICE, CHAT;
    }

    public static class TaskObject {
        private Task_TYPE TYPE;
        private String TOPIC;

        public TaskObject(Task_TYPE TYPE, String TOPIC) {
            this.TYPE = TYPE;
            this.TOPIC = TOPIC;
        }

        public String getTOPIC() {
            return TOPIC;
        }

        public Task_TYPE getTYPE() {
            return TYPE;
        }
    }

    public enum Task_TYPE {
        CREATE, ENTER, LEAVE;
    }
}
