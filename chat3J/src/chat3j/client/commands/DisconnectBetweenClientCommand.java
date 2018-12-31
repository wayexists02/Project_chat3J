package chat3j.client.commands;

import chat3j.client.Publisher;
import chat3j.messages.Message;
import com.esotericsoftware.kryonet.Connection;

/**
 * 미구현
 */
public class DisconnectBetweenClientCommand extends PublisherCommand {

    public DisconnectBetweenClientCommand() {

    }

    @Override
    public void exec(Publisher pub) {
        pub.disconnect();
    }
}
