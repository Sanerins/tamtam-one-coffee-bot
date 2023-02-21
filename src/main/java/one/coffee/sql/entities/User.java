package one.coffee.sql.entities;

import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;

import java.util.Objects;

public class User
        implements Entity {

    private long id;
    private String city;
    private long stateId;
    private long connectionId;

    public User(long id, String city, long stateId, long connectionId) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid User id! Got " + id);
        }

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("User's city can't be empty!");
        }

        this.id = id;
        this.city = city;
        this.stateId = stateId;
        this.connectionId = connectionId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public long getConnectedUserId() {
        if (connectionId <= 0) {
            throw new IllegalStateException(this + " has not connected user!");
        }

        UserConnection userConnection = UserConnectionsTable.getUserConnectionByUserId(id);
        return userConnection.getUser1Id() == id ? userConnection.getUser2Id() : userConnection.getUser1Id();
    }

    @Override
    public String sqlValues() {
        return String.format("(%d, '%s', %d, %d)", id, city, stateId, connectionId);
    }

    @Override
    public void commit() {
        UsersTable.putUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", stateId=" + stateId +
                ", connectionId=" + connectionId +
                '}';
    }
}
