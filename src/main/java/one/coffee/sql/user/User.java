package one.coffee.sql.user;

import java.lang.invoke.MethodHandles;

import one.coffee.sql.Entity;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User implements Entity {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private long id;
    private String city;
    private UserState state;
    private long connectionId;
    private String username;
    private String userInfo;

    public User(long id, String city, UserState state, long connectionId, String username, String userInfo) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.connectionId = connectionId;
        this.username = username;
        this.userInfo = userInfo;
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

    public static UserBuilder build() {
        return new UserBuilder();
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
                .append(StaticContext.quote(city))
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(state.ordinal())
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(connectionId)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(StaticContext.quote(username))
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(StaticContext.quote(userInfo))
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

    public static final class UserBuilder {

        private static final long DEFAULT_ID = SQLUtils.DEFAULT_ID;
        private static final String DEFAULT_CITY = "Cyberpunk2077";
        private static final UserState DEFAULT_STATE = UserState.DEFAULT;
        private static final long DEFAULT_CONNECTION_ID = SQLUtils.DEFAULT_ID;
        private static final String DEFAULT_USERNAME = "Вася Пупкин";
        private static final String DEFAULT_USERINFO = "Живу на болоте";

        private final User user =
                new User(DEFAULT_ID, DEFAULT_CITY, DEFAULT_STATE, DEFAULT_CONNECTION_ID, DEFAULT_USERNAME, DEFAULT_USERINFO);

        public UserBuilder setId(long id) {
            user.setId(id);
            return this;
        }

        public UserBuilder setCity(String city) {
            user.setCity(city);
            return this;
        }

        public UserBuilder setState(UserState state) {
            user.setState(state);
            return this;
        }

        public UserBuilder setConnectionId(long connectionId) {
            user.setConnectionId(connectionId);
            return this;
        }

        public UserBuilder setUsername(String username) {
            user.setUsername(username == null ? DEFAULT_USERNAME : username);
            return this;
        }

        public UserBuilder setUserInfo(String userInfo) {
            user.setUserInfo(userInfo == null ? DEFAULT_USERNAME : userInfo);
            return this;
        }

        public User get() {
            return user;
        }
    }

}
