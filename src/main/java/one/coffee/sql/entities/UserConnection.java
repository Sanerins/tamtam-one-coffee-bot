package one.coffee.sql.entities;

public class UserConnection {

    private long id;
    private long user1Id;
    private long user2Id;

    public UserConnection(long id, long user1Id, long user2Id) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(long user1Id) {
        this.user1Id = user1Id;
    }

    public long getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(long user2Id) {
        this.user2Id = user2Id;
    }
}
