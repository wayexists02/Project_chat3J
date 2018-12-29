package chat3j;

public abstract class Environment {

    public static final String os = System.getProperty("os.name");

    public static boolean isDesktop() {
        if (os.indexOf("win") >= 0 || os.indexOf("mac") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("nix") >= 0)
            return true;
        return false;
    }
}
