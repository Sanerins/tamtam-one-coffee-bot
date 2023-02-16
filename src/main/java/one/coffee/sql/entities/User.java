package one.coffee.sql.entities;

public class User implements Entity {

    private long id;
    private String city;
    private UserState userState;
    private UserConnection userConnection;

    public User(long id, String city, UserState userState, UserConnection userConnection) {
        this.id = id;
        this.city = city;
        this.userState = userState;
        this.userConnection = userConnection;
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

    public void setUserConnection(UserConnection userConnection) {
        this.userConnection = userConnection;
    }

    public User getConnectedUser() {
        return userConnection.getUser2();
    }

    @Override
    public String sqlValues() {
        return String.format("(%d, '%s', %d, %d)", id, city, userState.getStateType().getId(), userConnection.getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", userState=" + userState +
                ", userConnection=" + userConnection +
                '}';
    }
}
