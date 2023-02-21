package one.coffee.sql.entities;

import one.coffee.sql.DB;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;

public class UserConnection
        implements Entity {

    private long id;

    // No setters because if we change one of the users our connection will break
    private final long user1Id;
    private final long user2Id;

    public UserConnection(long user1Id, long user2Id) {
        this(-1, user1Id, user2Id);
    }

    public UserConnection(long id, long user1Id, long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;

        if (id <= 0) {
            UserConnectionsTable.putUserConnection(this);
            this.id = UserConnectionsTable.getUserConnectionByUserId(user1Id).getId();
        } else {
            if (!DB.hasEntityById(UserConnectionsTable.INSTANCE, id)) {
                throw new IllegalArgumentException("No UserConnection with 'id' = " + id);
            }
            this.id = id;
        }

        commit();
    }

    @Override
    public void commit() {
        commitUserConnection(id, UserState.CHATTING.getStateId());
    }

    // TODO Почистить ресурсы, чтобы к этому коннекшену нельзя было обращаться. По сути, предполагается, что данный метод - деструктор класса.
    public void breakConnection() {
        commitUserConnection(-1, UserState.DEFAULT.getStateId());
        UserConnectionsTable.deleteUserConnectionById(id); // TODO В будущем мы не будем удалять коннекшены, а будем менять их состояния
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

    public long getUser2Id() {
        return user2Id;
    }

    @Override
    public String toString() {
        return "UserConnection{" +
                "id=" + id +
                ", user1Id=" + user1Id +
                ", user2Id=" + user2Id +
                '}';
    }

    @Override
    public String sqlValues() {
        return String.format("(%d, %d)", user1Id, user2Id);
    }

    private void commitUserConnection(long connectionId, long stateId) {
        User user1 = UsersTable.getUserById(user1Id);
        user1.setConnectionId(connectionId);
        user1.setStateId(stateId);
        UsersTable.putUser(user1);

        User user2 = UsersTable.getUserById(user2Id);
        user2.setConnectionId(connectionId);
        user2.setStateId(stateId);
        UsersTable.putUser(user2);
    }

}
