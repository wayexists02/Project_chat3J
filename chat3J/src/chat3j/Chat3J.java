package chat3j;

import chat3j.client.Chat3JNode;
import chat3j.server.Chat3JMaster;

/**
 * 시스템 메니저 객체
 * NodeController와 ServerController를 생성해준다..
 */
public class Chat3J {

    // 이 객체는 오직 1개만 생성가능함
    private static Chat3J instance = new Chat3J();

    public static Chat3J getInstance() {
        return instance;
    }

    private Chat3J() {}

    // 해당 이름을 가진 새로운 노드를 생성한다
    public NodeController createNode(String name) {
        Chat3JNode node = new Chat3JNode(name);
        NodeController controller = new NodeController(node);

        return controller;
    }

    // 새로운 마스터를 생성한다.
    public MasterController createMaster() {
        Chat3JMaster master = new Chat3JMaster();
        MasterController controller = new MasterController(master);

        return controller;
    }
}
