package one.coffee.sql.entities;

import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;
import one.coffee.sql.utils.SqlUtils;

import java.sql.SQLException;

public class User
        implements Entity {

    private final long userId;
    private String city;
    private UserState state;
    private long connectionId;

    public User(long userId, String city, UserState state) throws SQLException {
        this(userId, city, state, SqlUtils.NO_ID);
    }

    public User(long userId, String city, UserState state, long connectionId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid User id! Got " + userId);
        }

        if (!isValidCity(city)) {
            throw new IllegalArgumentException("User's city can't be empty!");
        }

        this.userId = userId;
        this.city = city;
        this.state = state;
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
                .append(state).append(SqlUtils.ARGS_SEPARATOR)
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
                ", state=" + state +
                ", connectionId=" + connectionId +
                '}';
    }

    private static boolean isValidCity(String city) {
        return city != null && !city.trim().isEmpty();
    }
}
