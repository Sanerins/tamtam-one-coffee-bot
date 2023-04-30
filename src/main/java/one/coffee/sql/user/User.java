package one.coffee.sql.user;

import one.coffee.sql.Entity;
import one.coffee.sql.utils.UserState;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class User implements Entity {

    public static final String DEFAULT_CITY = "Cyberpunk2077";
    public static final UserState DEFAULT_STATE = UserState.DEFAULT;
    public static final String DEFAULT_USERNAME = "Вася Пупкин";
    public static final String DEFAULT_USERINFO = "Живу на болоте.";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final long id;
    private String city;
    private UserState state;
    private long connectionId;
    private String username;
    private String userInfo;

    public User(long id) {
        this(id, DEFAULT_CITY);
    }

    public User(long id, String city) {
        this(id, city, DEFAULT_STATE);
    }

    public User(long id, String city, UserState state) {
        this(id, city, state, SQLUtils.DEFAULT_ID);
    }

    public User(long id, String city, UserState state, long connectionId) {
        this(id, city, state, connectionId, DEFAULT_USERNAME);
    }

    public User(long id, String city, UserState state, long connectionId, String username) {
        this(id, city, state, connectionId, username, buildDefaultUserInfo(username));
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

    private static String buildDefaultUserInfo(String username) {
        return username + ". " + DEFAULT_USERINFO;
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
                .append(StaticContext.STRING_QUOTTER)
                .append(city)
                .append(StaticContext.STRING_QUOTTER)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(state.ordinal())
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(connectionId)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(StaticContext.STRING_QUOTTER)
                .append(username)
                .append(StaticContext.STRING_QUOTTER)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(StaticContext.STRING_QUOTTER)
                .append(userInfo)
                .append(StaticContext.STRING_QUOTTER)
                .append(SQLUtils.TABLE_SIGNATURE_END)
                .toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", city=" + StaticContext.STRING_QUOTTER + city + StaticContext.STRING_QUOTTER +
                ", state=" + state +
                ", connectionId=" + connectionId +
                ", username=" + username +
                ", userInfo=" + userInfo +
                '}';
    }

}
