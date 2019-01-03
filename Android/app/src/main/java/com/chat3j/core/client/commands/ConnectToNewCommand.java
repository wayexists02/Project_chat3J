package com.chat3j.core.client.commands;

import com.esotericsoftware.kryonet.Connection;

import com.chat3j.core.client.Chat3JNode;
import com.chat3j.core.messages.EnterTopicMsg;

/**
 * 토픽에 새로운 클라이언트가 입장했을 때, 새로운 클라이언트와 연결하기 위해 생성되는 명령
 */
public class ConnectToNewCommand extends PostCommand {

    public ConnectToNewCommand(Connection conn, EnterTopicMsg msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Chat3JNode node) {
        EnterTopicMsg response = (EnterTopicMsg) msg;
        // 새로운 클라이언트와 연결
        node.connectTo(response.topic, response.address, response.tcp, response.udp);
    }
}
