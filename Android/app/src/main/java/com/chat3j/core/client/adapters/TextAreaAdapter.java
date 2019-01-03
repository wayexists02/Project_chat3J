package com.chat3j.core.client.adapters;

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

    // 메세지를 출력하기 위한 메소드
    public abstract void write(String text);

    // 메세지를 큐에서 꺼내어 전달하는 메소드
    public abstract String read();

    // 메세지를 입력받아 큐에 넣는 메소드
    public abstract void inputQueue(String input);

    // 큐에 보낼 메세지가 있는지 확인하는 메소드
    public abstract boolean isEmpty();
}
