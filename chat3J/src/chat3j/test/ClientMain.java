package chat3j.test;

import chat3j.client.Chat3JNode;

public class ClientMain {

    public static void main(String[] args) {
        Chat3JNode node = new Chat3JNode("ABC");

        node.start();
    }
}
