package chat3j.test;

import chat3j.Chat3J;
import chat3j.MasterController;

public class MasterMain {

    public static void main(String[] args) {
        Chat3J chat3J = Chat3J.getInstance();
        MasterController master = chat3J.createMaster();

        master.open(10321, 10322);
    }
}
