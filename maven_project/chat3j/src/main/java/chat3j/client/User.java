package chat3j.client;

/**
 * 미구현
 */
public class User {

    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User)
            if (((User) obj).name.equals(this.name))
                return true;
        return false;
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
