package one.coffee.sql.entities;

public class UserConnection implements Entity {

    private long id;

    // No setters because if we change one of the users connection will break
    private final User user1;
    private final User user2;

    public UserConnection(User user1, User user2) {
        this(-1, user1, user2);
    }

    public UserConnection(long id, User user1, User user2) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    @Override
    public String toString() {
        return "UserConnection{" +
                ", user1=" + user1 +
                ", user2=" + user2 +
                '}';
    }

    @Override
    public String sqlValues() {
        return String.format("(%d, %d)", user1.getId(), user2.getId());
    }
}
