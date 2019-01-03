package chat3j.messages;

/**
 * 노드들 사이 텍스트 데이터를 실어나르는 메시지 객체
 */
public class TextDataMsg extends CommunicationDataMsg {

    // 텍스트 데이터
    public String textData;

    public TextDataMsg() {

    }

    /**
     * 데이터를 리턴함
     * @return
     */
    @Override
    public Object getData() {
        return textData;
    }

    /**
     * 데이터를 이 메시지 객체에 저장
     * @param obj
     */
    @Override
    public void setData(Object obj) {
        textData = (String) obj;
    }
}
