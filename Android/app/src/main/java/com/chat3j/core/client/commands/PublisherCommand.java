package com.chat3j.core.client.commands;

import com.esotericsoftware.kryonet.Connection;

import com.chat3j.core.client.Publisher;
import com.chat3j.core.messages.Message;

/**
 * 클라이언트들 사이에 오가는 메시지로 발생하는 명령.
 */
public abstract class PublisherCommand implements Comparable<PublisherCommand> {

    protected Connection conn;
    protected Message msg;

    public PublisherCommand(Connection conn, Message msg) {
        this.conn = conn;
        this.msg = msg;
    }
    public PublisherCommand() {
        this.conn = null;
        this.msg = null;
    }

    public abstract void exec(Publisher pub);

    @Override
    public int compareTo(PublisherCommand cmd) {
        return 0;
    }
}
