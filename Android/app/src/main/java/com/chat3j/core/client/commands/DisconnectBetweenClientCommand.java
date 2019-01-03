package com.chat3j.core.client.commands;

import com.chat3j.core.client.Publisher;

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
