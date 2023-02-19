package one.coffee.sql.entities;

import one.coffee.sql.DB;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;

import java.util.Objects;

public class UserConnection implements Entity {

    private long id;

    // No setters because if we change one of the users our connection will break
    private final User user1;
    private final User user2;

    public UserConnection(User user1, User user2) {
        this(-1, user1, user2);
    }

    public UserConnection(long id, User user1, User user2) {
        Objects.requireNonNull(user1, "User1 can't be null in UserConnection!");
        Objects.requireNonNull(user2, "User2 can't be null in UserConnection!");

        this.user1 = user1;
        this.user2 = user2;

        if (id <= 0) {
            UserConnectionsTable.putUserConnection(this); // Тут id не используется, всё пройдёт гладко
            this.id = 1;//UserConnectionsTable.getUserConnectionByUserId(user1.getId()).getId();
        } else {
            if (!DB.hasEntityById(UserConnectionsTable.INSTANCE, id)) {
                throw new IllegalArgumentException("No UserConnection with 'id'=" + id);
            }
            this.id = id;
        }

        user1.setUserConnection(this);
        user2.setUserConnection(this);

        user1.setState(UserState.CHATTING);
        user2.setState(UserState.CHATTING);

        // TODO Насколько мы тут ожидаем, что будут происходит транзакции с базой?
        UsersTable.putUser(user1);
        UsersTable.putUser(user2);
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
                "id=" + id +
                ", user1=" + user1 +
                ", user2=" + user2 +
                '}';
    }

    @Override
    public String sqlValues() {
        return String.format("(%d, %d)", user1.getId(), user2.getId());
    }
}
