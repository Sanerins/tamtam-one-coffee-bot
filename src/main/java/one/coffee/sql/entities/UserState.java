package one.coffee.sql.entities;

public class UserState {

    private long id;
    private String stateName;

    public UserState(long id, String stateName) {
        this.id = id;
        this.stateName = stateName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
