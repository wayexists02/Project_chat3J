package chat3j.messages;

/**
 * 토픽 리스트를 노드에게 알려줌
 */
public class UpdateTopicListMsg extends Message {

    public String[] topics;
    public String[] types;
}
