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

        checkIfUsersHaveDifferentConnections(user1, user2);

        this.user1 = user1;
        this.user2 = user2;

        if (id <= 0) {
            UserConnectionsTable.putUserConnection(this); // Тут id не используется, всё пройдёт гладко
            this.id = UserConnectionsTable.getUserConnectionsByUserId(user1.getId()).get(0).getId();
        } else {
            if (!DB.hasEntityById(UserConnectionsTable.INSTANCE, id)) {
                throw new IllegalArgumentException("No UserConnection with 'id' = " + id);
            }
            this.id = id;
        }

        commit();
    }

    // Создаёт номинальный коннекшен без транзакций с базой
    public void createNominalConnection() {
        setNominalUserConnection(this, UserState.CHATTING);
    }

    @Override
    public void commit() {
        commitUserConnection(this, UserState.CHATTING);
    }

    // TODO Почистить ресурсы, чтобы к этому коннекшену нельзя было обращаться. По сути, предполагается, что данный метод - деструктор класса.
    public void breakConnection() {
        if (!user1.getState().equals(UserState.CHATTING) || !user2.getState().equals(UserState.CHATTING)) {
            throw new IllegalStateException(user1 + " and " + user2 + " are in a non-consistent state!");
        }
        commitUserConnection(null, UserState.DEFAULT);
        UserConnectionsTable.deleteUserConnectionById(id); // TODO В будущем мы не будем удалять коннекшены, а будем менять их состояния
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

    private void setNominalUserConnection(UserConnection userConnection, UserState userState) {
        checkIfUsersHaveDifferentConnections(user1, user2);

        user1.setUserConnection(userConnection);
        user2.setUserConnection(userConnection);

        user1.setState(userState);
        user2.setState(userState);
    }

    // Меняет состояния user1 и user2 и коммитит их.
    private void commitUserConnection(UserConnection userConnection, UserState userState) {
        setNominalUserConnection(userConnection, userState);

        UsersTable.putUser(user1);
        UsersTable.putUser(user2);
    }

    private void checkIfUsersHaveDifferentConnections(User user1, User user2) {
        boolean user1HasConnection = user1.getUserConnection() != null;
        boolean user2HasConnection = user2.getUserConnection() != null;
        if (user1HasConnection && !user2HasConnection || !user1HasConnection && user2HasConnection
                || user1HasConnection && !user1.getUserConnection().equals(user2.getUserConnection())) {
            throw new IllegalStateException(user1 + " and " + user2 + " have already connected!");
        }
    }

}
