package chat3j.client.commands;

import chat3j.client.Publisher;
import chat3j.client.data.ByteArray;
import chat3j.client.data.VoiceData;
import chat3j.messages.VoiceDataMsg;
import com.esotericsoftware.kryonet.Connection;

public class CommunicationInputCommand extends PublisherCommand {

    public CommunicationInputCommand(Connection conn, VoiceDataMsg msg) {
        super(conn, msg);
    }

    @Override
    public void exec(Publisher pub) {
        VoiceDataMsg dataMsg = (VoiceDataMsg) msg;
        ByteArray ba = new ByteArray();
        ba.data = dataMsg.data;

        VoiceData data = new VoiceData();
        data.setData(ba);

        pub.communicate(data);
    }
}
