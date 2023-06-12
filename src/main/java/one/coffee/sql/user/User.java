package one.coffee.sql.user;

import one.coffee.sql.DB;
import one.coffee.sql.Entity;
import one.coffee.sql.states.UserState;
import one.coffee.sql.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class User implements Entity {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final long id;
    private String city;
    private UserState state;
    private long connectionId;
    private String username;
    private String userInfo;

    // От этого надо избавиться
    public User(long id, String city, UserState state, String username) {
        this(id, city, state, SQLUtils.DEFAULT_ID, username, "");
    }

    public User(long id, String city, UserState state, long connectionId, String username, String userInfo) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.connectionId = connectionId;
        this.username = username;
        this.userInfo = userInfo;
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

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isNotChatting() {
        return !UserState.CHATTING.equals(state);
    }
    @Override
    public boolean isCreated() {
        return true; // Because we don't create a new 'id' for each user, we use in-built userId
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String sqlArgValues() {
        return new StringBuilder(SQLUtils.TABLE_SIGNATURE_START)
                .append(id)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(DB.quote(city))
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(state.ordinal())
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(connectionId)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(DB.quote(username))
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(DB.quote(userInfo))
                .append(SQLUtils.TABLE_SIGNATURE_END)
                .toString();
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
