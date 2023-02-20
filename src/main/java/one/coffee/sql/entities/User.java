package one.coffee.sql.entities;

import one.coffee.sql.tables.UsersTable;

import java.util.Objects;

public class User implements Entity {

    private long id;
    private String city;
    private UserState userState;
    private UserConnection userConnection;

    public User(long id, String city, UserState userState, UserConnection userConnection) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid User id! Got " + id);
        }

        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("User's city can't be empty!");
        }

        Objects.requireNonNull(userState, "UserState can't be null!");

        this.id = id;
        this.city = city;
        this.userState = userState;
        this.userConnection = userConnection;

        commit();
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

    public UserState getState() {
        return userState;
    }

    public void setState(UserState userState) {
        this.userState = userState;
    }

    public UserConnection getUserConnection() {
        return userConnection;
    }

    public long getUserConnectionId() {
        Objects.requireNonNull(userConnection, "UserConnection is null!");
        return userConnection.getId();
    }

    public void setUserConnection(UserConnection userConnection) {
        Objects.requireNonNull(userConnection, "UserConnection is null!");
        this.userConnection = userConnection;
    }

    public User getConnectedUser() {
        Objects.requireNonNull(userConnection, "UserConnection is null!");
        return userConnection.getUser1().getId() == id ? userConnection.getUser2() : userConnection.getUser1(); // FIXME
    }

    @Override
    public String sqlValues() {
        return String.format("(%d, '%s', %d, %d)", id, city, userState.getId(), userConnection == null ? 0 : userConnection.getId());
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
                ", stateId=" + userState.getId() +
                ", userConnectionId=" + (userConnection == null ? 0 : userConnection.getId()) + // Escape reflexive calls to UserConnection::toString
                '}';
    }
}
