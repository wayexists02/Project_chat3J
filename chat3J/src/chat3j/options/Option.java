package chat3j.options;

/**
 * 외부 요청에 대한 응답
 * 요청한 연산이 완료되면 ok = true로 세팅
 */
public class Option<T> {

    public boolean ok; // 연산모두끝남?
    public T data; // 연산에 의한 데이터 리턴값이 있을 경우 설정, 없으면 boolean으로 설정해서 에러체크용으로.
    public String message; // 에러 메시지 or 'Success.' or 필요한 메시지

    public Option() {
        this.ok = false;
        this.message = "";
    }

    // 이 옵션과 짝을 이루는 연산이 모두 끝나면 ok=true로 설정할 것이다. 그때까지 기다림.
    public void waitFor() {
        while (!ok)
            Thread.yield();
    }
}
