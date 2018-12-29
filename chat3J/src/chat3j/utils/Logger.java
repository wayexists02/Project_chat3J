package chat3j.utils;

import java.util.Calendar;

public class Logger {

    private static Logger logger = new Logger(true);
    private static Logger serverLogger = new Logger(true);

    private boolean log;

    public static Logger getLogger() {
        return logger;
    }

    public static Logger getServerLogger() {
        return serverLogger;
    }

    private Logger(boolean log) {
        this.log = log;
    }

    public String info(String msg) {
        if (!log) return null;

        String log = log(msg);
        System.out.println(log);
        return log;
    }

    public String error(String msg) {
        if (!log) return null;

        String log = log(msg);
        System.err.println(log);
        return log;
    }

    private String log(String msg) {
        if (!log) return null;

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
