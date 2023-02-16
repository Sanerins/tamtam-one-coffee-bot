package one.coffee.sql.entities;

public class User {

    private long id;
    private UserState userState;
    private UserConnection userConnection;
    private String city;


    public User(long id, UserState userState, UserConnection userConnection, String city) {
        this.id = id;
        this.userState = userState;
        this.userConnection = userConnection;
        this.city = city;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
