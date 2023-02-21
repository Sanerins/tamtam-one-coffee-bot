package one.coffee.sql.entities;

import one.coffee.sql.DB;
import one.coffee.sql.Utils;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;

@CommitOnCreate
public class UserConnection
        implements Entity {

    private final long id;
    private final long user1Id;
    private final long user2Id;
    private final boolean isCreated;

    public UserConnection(long user1Id, long user2Id) {
        this(-1, user1Id, user2Id);
    }

    public UserConnection(long id, long user1Id, long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;

        if (id <= 0) {
            commit();
            this.id = UserConnectionsTable.getUserConnectionByUserId(user1Id).getId();
        } else {
            if (!DB.hasEntityById(UserConnectionsTable.INSTANCE, id)) {
                throw new IllegalArgumentException("No UserConnection with 'id' = " + id);
            }
            this.id = id;
        }
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

    @Override
    public boolean isCreated() {
        return isCreated;
    }

    public long getId() {
        return id;
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
    public String sqlArgValues() {
        StringBuilder sqlValues = new StringBuilder(Utils.SIGNATURE_START);
        if (isCreated()) {
            sqlValues.append(id).append(Utils.ARGS_SEPARATOR);
        }
        sqlValues.append(Utils.SIGNATURE_END);
        return sqlValues.toString();
    }

    private void commitUserConnection(long connectionId, long stateId) {
        User user1 = UsersTable.getUserByUserId(user1Id);
        user1.setConnectionId(connectionId);
        user1.setStateId(stateId);
        UsersTable.putUser(user1);

        User user2 = UsersTable.getUserByUserId(user2Id);
        user2.setConnectionId(connectionId);
        user2.setStateId(stateId);
        UsersTable.putUser(user2);
    }

}
