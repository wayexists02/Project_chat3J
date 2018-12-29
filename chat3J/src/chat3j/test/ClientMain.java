package chat3j.test;

import chat3j.Chat3J;
import chat3j.NodeController;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        System.out.println("Node Start!");

        Chat3J inst = Chat3J.getInstance();
        NodeController controller = inst.createNode("Alice");
        controller.setMasterInformation("localhost", 10321, 10322);

        String input;
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Type operation: ");
            input = sc.nextLine();

            if (input.equals("create topic")) {
                String topic = sc.nextLine();
                controller.createTopic(topic);
            }
            else if (input.equals("exit")) {
                controller.close();
            }
            else {
                String[] line = input.split(" ");
                if (line.length > 1) {
                    if (line[0].equals("exit")) {
                        controller.exitFromTopic(line[1]);
                    }
                }
            }
        }
    }
}
