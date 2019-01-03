package chat3j.client.commands;

import chat3j.client.Publisher;
import chat3j.client.data.ByteArray;
import chat3j.client.data.TextData;
import chat3j.client.data.VoiceData;
import chat3j.messages.CommunicationDataMsg;
import chat3j.messages.TextDataMsg;
import chat3j.messages.VoiceDataMsg;
import com.esotericsoftware.kryonet.Connection;

/**
 * 텍스트나 음성 통신을 받으면 생성 및 호출되는 명령
 */
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
