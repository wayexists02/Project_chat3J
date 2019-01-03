
import chat3j.*;
import chat3j.options.Option;

import javax.sound.sampled.*;
import java.util.List;
import java.util.Scanner;

public class ClientMain {

    // javac -cp Class.jar Class.java
    // java -cp .:Class.jar Class

    public static void main(String[] args) {

        /*Chat3JNode node = new Chat3JNode("ABC");

        node.start();*/
        Chat3J chat3j = Chat3J.getInstance();
        NodeController nodeController = chat3j.createNode("testnode");
        nodeController.setMasterInformation("localhost",10321,10322);
        //nodeController.setMasterInformation("35.221.176.240",10321,10322);
        nodeController.open();

        Source source = new Source();
        Target target = new Target();

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
            System.out.println("6. quit(if disconnected)");
            input_i = sc.nextInt();sc.nextLine();

            switch(input_i) {
                case 1:
                    System.out.println("Type name of Topic.");
                    input_s = sc.nextLine();
                    nodeController.createTopic(input_s, NodeController.CommunicationType.VOICE, source, target);
                    break;
                case 2:
                    System.out.println("Type name of Topic.");
                    input_s = sc.nextLine();
                    nodeController.enterTopic(input_s, source, target);
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
                case 6:
                    exit = true;//새로 추가한 코드 서버가 종료되었을 경우 사용한다.
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

class Source implements Chat3JSourceDevice {

    private SourceDataLine sourceLine;

    public Source() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

            sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
            sourceLine.open(format);
            sourceLine.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeData(CommunicationData cData) {
        ByteArrayData data = (ByteArrayData) cData;
        sourceLine.write(data.data, 0, data.size);
    }
}

class Target implements Chat3JTargetDevice {

    private TargetDataLine targetLine;

    public Target() {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);

            targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetLine.open(format);
            targetLine.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommunicationData readData() {
        ByteArrayData data = new ByteArrayData();
        data.data = new byte[44100/25];
        data.size = targetLine.read(data.data, 0, 44100 / 25);
        return data;
    }
}
