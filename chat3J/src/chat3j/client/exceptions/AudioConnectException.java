package chat3j.client.exceptions;

public class AudioConnectException extends ChatException {

    public boolean device;
    public boolean recorder;

    public AudioConnectException() {
        device = false;
        recorder = false;
    }
}
