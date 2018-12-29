package chat3j.test;

import chat3j.Chat3J;
import chat3j.MasterController;

import java.util.Scanner;

public class MasterMain {

    public static void main(String[] args) {
        Chat3J chat3J = Chat3J.getInstance();
        MasterController master = chat3J.createMaster();

        master.open(10321, 10322);
        Scanner sc = new Scanner(System.in);
        System.out.println("press any key to exit!");
        sc.nextLine();
        master.close();
    }
}
