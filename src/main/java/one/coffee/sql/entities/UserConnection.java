package one.coffee.sql.entities;

import one.coffee.sql.DB;
import one.coffee.sql.utils.Utils;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;

import java.sql.SQLException;

@CommitOnCreate
public class UserConnection
        implements Entity {

    @Argument
    private final long id;
    @Argument
    private final long user1Id;
    @Argument
    private final long user2Id;
    private final boolean isCreated;

    public UserConnection(long user1Id, long user2Id) throws SQLException {
        this(-1, user1Id, user2Id);
    }

    public UserConnection(long id, long user1Id, long user2Id) throws SQLException {
        this(id, UsersTable.getUserByUserId(user1Id), UsersTable.getUserByUserId(user2Id));
    }

    public UserConnection(User user1, User user2) throws SQLException {
        this(-1, user1, user2);
    }

    public UserConnection(long id, User user1, User user2) throws SQLException {
        if (id <= 0 &&
                (user1.getConnectionId() >= 1 || user2.getConnectionId() >= 1)) {
            throw new IllegalArgumentException(user1 + " or " + user2 + " has already connected!");
        }

        this.user1Id = user1.getUserId();
        this.user2Id = user2.getUserId();

        if (id <= 0) {
            commit();
            this.id = UserConnectionsTable.getUserConnectionByUserId(user1Id).getId();
            commitUserConnection(this.id, UserState.CHATTING.getStateId(), user1, user2);
        } else {
            this.id = id;
            if (!DB.hasEntity(UserConnectionsTable.INSTANCE, this)) {
                throw new IllegalArgumentException("No UserConnection with 'id' = " + id);
            }
        }

        this.isCreated = true;
    }

    @Override
    public void commit() {
        UserConnectionsTable.putUserConnection(this);
    }

    // TODO Почистить ресурсы, чтобы к этому коннекшену нельзя было обращаться. По сути, предполагается, что данный метод - деструктор класса.
    public void breakConnection() throws SQLException {
        User user1 = UsersTable.getUserByUserId(user1Id);
        User user2 = UsersTable.getUserByUserId(user2Id);
        breakConnection(user1, user2);
    }

    public void breakConnection(User user1, User user2) throws SQLException {
        commitUserConnection(-1, UserState.DEFAULT.getId(), user1, user2);
        UserConnectionsTable.deleteUserConnection(this); // TODO В будущем мы не будем удалять коннекшены, а будем менять их состояния
    }

    @Override
    public boolean isCreated() {
        return isCreated;
    }

    @Override
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

        // TODO Оптимизировать, чтобы ручками не вводить каждый аргумент. Сделать список из аннотированных элементов @Argument
        // и добавлять их. При таком подходе метод будет один на всех.
        sqlValues.append(user1Id).append(Utils.ARGS_SEPARATOR)
                .append(user2Id);

        sqlValues.append(Utils.SIGNATURE_END);
        return sqlValues.toString();
    }

    private void commitUserConnection(long connectionId, long stateId, User user1, User user2) {
        user1.setStateId(stateId);
        user1.setConnectionId(connectionId);
        user1.commit();

        user2.setStateId(stateId);
        user2.setConnectionId(connectionId);
        user2.commit();
    }

}
