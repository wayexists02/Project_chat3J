package com.chat3j.android;

import android.app.Activity;
import android.widget.EditText;

import com.chat3j.core.client.adapters.TextAreaAdapter;

/**
 * 안드로이드의 TextArea에 대한 operation을 여기서 처리
 */

public class AndroidTextArea extends TextAreaAdapter {

    /**
     * 전역변수로 androidTextArea에 해당하는 변수 선언
     */

    EditText editText_Chat;
    TextMessageAdapter ChatAdapter;
    Activity activity;

    // 안드로이드에서 채팅 메세지를 입력받을 EditText, 리스트뷰에 출력하기 위한 Adapter
    // UI 작업을 위한 Activity를 입력받음
    public AndroidTextArea(EditText editText, TextMessageAdapter adapter, Activity activity) {
        editText_Chat = editText;
        ChatAdapter = adapter;
        this.activity = activity;
    }

    // 메세지를 Adapter에 추가해주고 출력해주는 메소드
    @Override
    public void write(String data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatAdapter.addItem(new ChatItem(data));
                ChatAdapter.notifyDataSetChanged();
            }
        });
    }

    // 메세지를 Queue에서 꺼내서 리턴하는 메소드
    @Override
    public String read() {
        String Message = textQueue.poll();
        write(Message);
        return Message;
    }

    // Queue에 보낼 메세지를 저장하는 메소드
    @Override
    public void inputQueue(String input) {
        textQueue.offer(input);
    }

    // 메시지 큐가 비어있는지 확인하는 메소드
    @Override
    public boolean isEmpty() {
        return textQueue.isEmpty();
    }

    // 현재 선택된 Topic의 대화를 보여주기위해 Adapter를 리턴해주는 메소드
    public TextMessageAdapter getChatAdapter() {
        return ChatAdapter;
    }

}
