package chat3j.client.commands;

import chat3j.client.Publisher;

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
