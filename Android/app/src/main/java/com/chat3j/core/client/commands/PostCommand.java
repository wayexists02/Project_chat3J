package com.chat3j.core.client.commands;

import com.esotericsoftware.kryonet.Connection;

import com.chat3j.core.messages.Message;

/**
 * 마스터와 클라이언트간 메시지로 인해 생성되는 명령
 */
public abstract class PostCommand extends Command {

    public Message msg;
    public Connection conn;

    public PostCommand(Connection conn, Message msg) {
        this.conn = conn;
        this.msg = msg;
    }
}
