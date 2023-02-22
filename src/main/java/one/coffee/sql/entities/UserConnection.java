package one.coffee.sql.entities;

import one.coffee.sql.DB;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;
import one.coffee.sql.utils.SqlUtils;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

public class UserConnection
        implements Entity {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private long id;
    private long user1Id;
    private long user2Id;
    private boolean isCreated;
    private boolean isCommitted;

    public UserConnection(long user1Id, long user2Id) throws SQLException {
        this(StaticContext.NO_ID, user1Id, user2Id);
    }

    public UserConnection(long id, long user1Id, long user2Id) throws SQLException {
        this(id, UsersTable.getUserByUserId(user1Id), UsersTable.getUserByUserId(user2Id));
    }

    public UserConnection(long id, User user1, User user2) throws SQLException {
        if (id <= 0 &&
                (user1.hasConnection() || user2.hasConnection())) {
            throw new IllegalArgumentException(user1 + " or " + user2 + " has already connected!");
        }

        this.user1Id = user1.getId();
        this.user2Id = user2.getId();

        if (id >= 1) {
            this.id = id;
            if (!DB.hasEntity(UserConnectionsTable.INSTANCE, this)) {
                throw new IllegalArgumentException("No UserConnection with 'id' = " + id);
            }
            this.isCreated = true;
        }
    }

    @Override
    public void commit() throws SQLException {
        if (isCommitted) {
            LOG.warn("Connection has already built between users: {} and {}", user1Id, user2Id);
            return;
        }

        UserConnectionsTable.putUserConnection(this);
        this.id = UserConnectionsTable.getUserConnectionByUserId(user1Id).getId();
        commitUsersConnection(this.id, UserState.CHATTING.getStateId());
        this.isCreated = true;
        this.isCommitted = true;
    }

    // Деструктор класса. Обращение к его полям и методам после вызова этой функции может привести к неожиданному поведению.
    public void breakConnection() throws SQLException {
        commitUsersConnection(StaticContext.NO_ID, UserState.DEFAULT.getId());

        // TODO В будущем мы не будем удалять коннекшены, а будем менять их состояния
        UserConnectionsTable.deleteUserConnection(this);

        this.id = StaticContext.NO_ID;
        this.user1Id = StaticContext.NO_ID;
        this.user2Id = StaticContext.NO_ID;
        this.isCreated = false;
    }

    private void commitUsersConnection(long connectionId, long stateId) throws SQLException {
        User user1 = UsersTable.getUserByUserId(user1Id);
        user1.setStateId(stateId);
        user1.setConnectionId(connectionId);
        user1.commit();

        User user2 = UsersTable.getUserByUserId(user2Id);
        user2.setStateId(stateId);
        user2.setConnectionId(connectionId);
        user2.commit();
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
        StringBuilder sqlValues = new StringBuilder(SqlUtils.SIGNATURE_START);

        if (isCreated()) {
            sqlValues.append(id).append(SqlUtils.ARGS_SEPARATOR);
        }

        return sqlValues.append(user1Id).append(SqlUtils.ARGS_SEPARATOR)
                .append(user2Id).append(SqlUtils.SIGNATURE_END).toString();
    }

}
