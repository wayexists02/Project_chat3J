package chat3j.client.adapters;

/**
 * 미구현.
 * 안드로이드의 TextArea에 대한 operation을 여기서 처리
 */
public class AndroidTextArea extends TextAreaAdapter {

    /**
     * 전역변수로 androidTextArea에 해당하는 변수 선언
     */
    public AndroidTextArea() {
        
    }

    /**
     * androidTextArea.setText(data);
     * 이런식으로 이 메소드 내에서 호출하면 됨
     * @param data
     */
    @Override
    public void write(String data) {

    }

    /**
     * androidTextArea.getText();
     * 이런식으로 이 메소드 내에서 텍스트 얻어오고 그거 그냥 리턴해주면 됨.
     * @return
     */
    @Override
    public String read() {
        return null;
    }
}
