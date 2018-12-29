package chat3j.test;

import chat3j.Chat3J;
import chat3j.NodeController;
import chat3j.client.Chat3JNode;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {

        /*Chat3JNode node = new Chat3JNode("ABC");

        node.start();*/
        Chat3J chat3j = Chat3J.getInstance();
        NodeController nodeController = chat3j.createNode("testnode");
        nodeController.setMasterInformation("127.1.1.0",10321,10322);
        nodeController.open();

        int input_i;
        String input_s;
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        while(!exit) {

            System.out.println("1. Create Topic");
            System.out.println("2. Enter Topic");
            System.out.println("3. Remove Topic");
            System.out.println("4. quit");
            input_i = sc.nextInt();sc.nextLine();

            switch(input_i) {
                case 1:
                    System.out.println("Type name of Topic.");
                    input_s = sc.nextLine();
                    nodeController.createTopic(input_s);
                    break;
                case 2:
                    System.out.println("Type name of Topic.");
                    input_s = sc.nextLine();
                    nodeController.enterTopic(input_s);
                    break;
                case 3:
                    System.out.println("Type name of Topic.");
                    input_s = sc.nextLine();
                    nodeController.exitFromTopic(input_s);
                    break;
                case 4:
                    /*Chat3JNode 닫기옵션 미구현*/
                    nodeController.close();
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }

        }

    }
}
