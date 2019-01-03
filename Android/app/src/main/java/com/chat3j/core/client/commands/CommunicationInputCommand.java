package com.chat3j.core.client.commands;

import com.esotericsoftware.kryonet.Connection;

import com.chat3j.core.client.Publisher;
import com.chat3j.core.client.data.ByteArray;
import com.chat3j.core.client.data.TextData;
import com.chat3j.core.client.data.VoiceData;
import com.chat3j.core.messages.CommunicationDataMsg;
import com.chat3j.core.messages.TextDataMsg;
import com.chat3j.core.messages.VoiceDataMsg;

public class CommunicationInputCommand extends PublisherCommand {

    public CommunicationInputCommand(Connection conn, CommunicationDataMsg msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Publisher pub) {
        CommunicationDataMsg dataMsg = (CommunicationDataMsg) msg;

        if (dataMsg.type.equals("Voice")) { // 만약, 음성 메시지의 경우
            ByteArray ba = new ByteArray();
            ba.data = ((VoiceDataMsg) dataMsg).data;

            VoiceData data = new VoiceData();
            data.setData(ba);

            pub.communicate(data);
        }
        else if (dataMsg.type.equals("Chat")) { // 만약, 텍스트 메시지의 경우
            String str = ((TextDataMsg) dataMsg).textData;

            TextData data = new TextData();
            data.setData(str);

            pub.communicate(data);
        }
        else { // 그 이외의 경우
            // error
        }
    }
}
