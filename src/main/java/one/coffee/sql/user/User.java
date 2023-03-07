package one.coffee.sql.user;

import one.coffee.sql.Entity;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.utils.SQLUtils;

import java.sql.SQLException;

public class User implements Entity {

    private final long id;
    private String city;
    private UserState state;
    private long connectionId;

    public User(long id, String city, long stateId) {
        this(id, city, stateId, SQLUtils.NO_ID);
    }

    public User(long id, String city, long stateId, long connectionId) {
        this(id, city, UserState.fromId(stateId), connectionId);
    }

    public User(long id, String city, UserState state) {
        this(id, city, state, SQLUtils.NO_ID);
    }

    public User(long id, String city, UserState state, long connectionId) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.connectionId = connectionId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public boolean hasConnection() throws SQLException {
        return UsersTable.getUserByUserId(id).getConnectionId() >= 1;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public long getConnectedUserId() throws SQLException {
        if (connectionId <= 0) {
            throw new IllegalStateException(this + " has not connected user!");
        }

        UserConnection userConnection = UserConnectionsTable.getUserConnectionUserById(id);
        return userConnection.getUser1Id() == id ? userConnection.getUser2Id() : userConnection.getUser1Id();
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public String sqlArgValues() {
        return new StringBuilder(SQLUtils.TABLE_SIGNATURE_START)
                .append(id).append(SQLUtils.ARGS_SEPARATOR)
                .append(SQLUtils.STRING_QUOTTER).append(city).append(SQLUtils.STRING_QUOTTER).append(SQLUtils.ARGS_SEPARATOR)
                .append(state.ordinal()).append(SQLUtils.ARGS_SEPARATOR)
                .append(connectionId)
                .append(SQLUtils.TABLE_SIGNATURE_END).toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", state=" + state +
                ", connectionId=" + connectionId +
                '}';
    }

}
