package chat3j.client.commands;

import chat3j.client.Chat3JNode;

/**
 * 사용자가 입력을 넣어서 발생하는 명령 (Command)
 * 또는
 * 마스터에게 메시지를 받아 발생하는 명령 (PostCommand)
 *
 *                             Command                          PublisherCommand
 *                      Command   PostCommand
 */
public abstract class Command implements Comparable<Command> {

    public Command() {

    }

    @Override
    public int compareTo(Command cmd) {
        return 0;
    }

    public abstract void exec(Chat3JNode node); // 명령 수행 메소드
}
