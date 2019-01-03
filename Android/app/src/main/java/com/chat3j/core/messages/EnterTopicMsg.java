package com.chat3j.core.messages;

/**
 * 클라이언트가 어떤 토픽에 입장하겠다는 의사를 마스터에게 전달한 후,
 * 그 클라이언트가 모든 준비를 마치면, 이 메시지를 토픽의 기존 인원들에게
 * 브로드캐스트
 *
 * --토픽 입장 순서
 * 클라이언트 -> 마스터 (RequestTopicMsg) 요청
 * 마스터 -> 클라이언트 (RequestTopicMsg) 답신 (요청한 토픽이 존재하나)
 * 클라이언트 -> 마스터 (ReadyForEnterMsg) 연결준비완료
 * 마스터 -> 토픽의 다른 클라이언트 (EnterTopicMsg) 새로운 클라이언트와 연결하라
 */
public class EnterTopicMsg extends Message {

    // send to client
    public String topic;
    public String address;
    public int tcp;
    public int udp;

    public EnterTopicMsg() {

    }
}
