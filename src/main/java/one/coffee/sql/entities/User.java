package one.coffee.sql.entities;

import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;
import one.coffee.sql.utils.SqlUtils;

import java.sql.SQLException;

public class User
        implements Entity {

    private final long userId;
    private String city;
    private long stateId;
    private long connectionId;

    public User(long userId, String city, long stateId) throws SQLException {
        this(userId, city, stateId, -1);
    }

    public User(long userId, String city, long stateId, long connectionId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid User id! Got " + userId);
        }

        if (!isValidCity(city)) {
            throw new IllegalArgumentException("User's city can't be empty!");
        }

        UserState.StateType.fromId(stateId); // Check for stateId existence

        this.userId = userId;
        this.city = city;
        this.stateId = stateId;
        this.connectionId = connectionId;
    }

    @Override
    public long getId() {
        return userId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public boolean hasConnection() throws SQLException {
        return UsersTable.getUserByUserId(userId).getConnectionId() >= 1;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public long getConnectedUserId() throws SQLException {
        if (connectionId <= 0) {
            throw new IllegalStateException(this + " has not connected user!");
        }

        UserConnection userConnection = UserConnectionsTable.getUserConnectionByUserId(userId);
        return userConnection.getUser1Id() == userId ? userConnection.getUser2Id() : userConnection.getUser1Id();
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public String sqlArgValues() {
        return new StringBuilder(SqlUtils.SIGNATURE_START)
                .append(userId).append(SqlUtils.ARGS_SEPARATOR)
                .append(SqlUtils.STRING_QUOTTER).append(city).append(SqlUtils.STRING_QUOTTER).append(SqlUtils.ARGS_SEPARATOR)
                .append(stateId).append(SqlUtils.ARGS_SEPARATOR)
                .append(connectionId)
                .append(SqlUtils.SIGNATURE_END).toString();
    }

    @Override
    public void commit() throws SQLException {
        UsersTable.putUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", city='" + city + '\'' +
                ", stateId=" + stateId +
                ", connectionId=" + connectionId +
                '}';
    }

    private static boolean isValidCity(String city) {
        return city != null && !city.trim().isEmpty();
    }
}
