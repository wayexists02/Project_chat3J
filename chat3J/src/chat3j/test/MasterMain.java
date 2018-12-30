package chat3j.test;

import chat3j.server.Chat3JMaster;

public class MasterMain {

    public static void main(String[] args) {
        Chat3JMaster master = new Chat3JMaster();
        master.start();
    }
}
