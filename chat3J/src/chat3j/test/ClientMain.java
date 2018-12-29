package chat3j.test;

import chat3j.Chat3J;
import chat3j.NodeController;
import chat3j.options.Option;

import java.util.List;
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
            System.out.println("4. Print Topic List");
            System.out.println("5. quit");
            input_i = sc.nextInt();sc.nextLine();

            switch(input_i) {
                case 1:
                    System.out.println("Type name of Topic.");
                    input_s = sc.nextLine();
                    nodeController.createTopic(input_s, NodeController.CommunicationType.VOICE);
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
                    Option<List<String>> list = nodeController.requestForTopicList();
                    list.data.forEach(System.out::println);
                    break;
                case 5:
                    Option<Boolean> opt = nodeController.close();
                    opt.waitFor();
                    //System.out.println(opt.message);
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
        }

        /*
        for (Thread t: Thread.getAllStackTraces().keySet())
            System.out.println(t.getName());
            */
    }
}
