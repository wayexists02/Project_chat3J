package com.chat3j.core.messages;

/**
 * 노드간 통신중에서 텍스트나 음성 데이터가 저장되는 메시지
 */
public class CommunicationDataMsg extends Message {

    // "Voice", "Chat" 중 하나
    public String type;

    public CommunicationDataMsg() {

    }

    /**
     * 보이스 또는 텍스트 데이터를 얻어온다.
     * 반드시 오버라이딩 필요
     * @return
     */
    public Object getData() {
        return null;
    }

    /**
     * 이 메시지 객체에 데이터를 저장함.
     * 반드시 오버라이딩 필요
     * @param obj
     */
    public void setData(Object obj) {
        // need to be overrided
    }
}
