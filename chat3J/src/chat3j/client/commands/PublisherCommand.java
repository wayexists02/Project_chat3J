package chat3j.client.commands;

import chat3j.client.Publisher;

/**
 * 클라이언트들 사이에 오가는 메시지로 발생하는 명령.
 */
public abstract class PublisherCommand implements Comparable<PublisherCommand> {

    public abstract void exec(Publisher pub);

    @Override
    public int compareTo(PublisherCommand cmd) {
        return 0;
    }
}
