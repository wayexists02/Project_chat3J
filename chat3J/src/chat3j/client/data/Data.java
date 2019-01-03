package chat3j.client.data;

import chat3j.CommunicationData;

/**
 * 데이터의 최상위 클래스
 */
public abstract class Data {

    protected CommunicationData data;

    public void setData(CommunicationData data) {
        this.data = data;
    }
    public CommunicationData getData() {
        return data;
    }
}
