package chat3j.utils;

import java.util.Calendar;

public class Logger {

    private static Logger logger = new Logger();

    public static Logger getLogger() {
        return logger;
    }

    private Logger() {

    }

    public String info(String msg) {
        String log = log(msg);
        System.out.println(log);
        return log;
    }

    public String error(String msg) {
        String log = log(msg);
        System.err.println(log);
        return log;
    }

    private String log(String msg) {
        String log = prefix();
        log += msg;

        return log;
    }

    private String prefix() {
        Calendar cal = Calendar.getInstance();
        String prefix = "[ " + cal.getTime().toString() + " ] ";

        return prefix;
    }
}
