package chat3j.client.commands;

import chat3j.client.Publisher;
import chat3j.messages.Message;
import com.esotericsoftware.kryonet.Connection;

/**
 * 노드들 사이 연결을 끊을 때 생성되는 명령
 */
public class DisconnectBetweenClientCommand extends PublisherCommand {

    public DisconnectBetweenClientCommand() {

    }

    @Override
    public void exec(Publisher pub) {
        pub.disconnect();
    }
}
