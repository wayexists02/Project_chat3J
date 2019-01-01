package chat3j.client.adapters;

/**
 * 안드로이드 뿐만 아니라 다른 Textarea 도 수용 가능하게 하기 위한 어댑터
 */
public abstract class TextAreaAdapter {

    public TextAreaAdapter() {

    }

    public abstract void write(String text);
    public abstract String read();
}
