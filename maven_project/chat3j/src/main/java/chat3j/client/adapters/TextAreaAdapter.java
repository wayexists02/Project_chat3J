package chat3j.client.adapters;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 안드로이드 뿐만 아니라 다른 Textarea 도 수용 가능하게 하기 위한 어댑터
 */
public abstract class TextAreaAdapter {
    // 보낼 메세지가 있는지 확인하는 큐
    protected Queue<String> textQueue;

    public TextAreaAdapter() {
        textQueue = new LinkedList<>();
    }

    public abstract void write(String text);

    public abstract String read();

    public abstract boolean isEmpty();
}
