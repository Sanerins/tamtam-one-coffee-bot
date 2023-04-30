package one.coffee.sql.user;

import one.coffee.sql.Entity;
import one.coffee.sql.UserState;
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

    // ЧТО ЭТО ЗА КОНСТРУКТОР?? ПОЧЕМУ КОНСТРУКТОРЫ ДЛЯ ТЕСТОВ ТУТ
    public User(long id, String city, UserState state, String username) {
        this(id, city, state, SQLUtils.NO_ID, username);
    }

    public User(long id, String city, UserState state, long connectionId, String username) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.connectionId = connectionId;
        this.username = username;
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

//    public long getConnectedUserId() throws SQLException {
//        if (connectionId <= 0) {
//            LOG.warn(this + " has not connected user!");
//            return SQLUtils.NO_ID;
//        }
//
//        UserConnection userConnection = UserConnectionsTable.getUserConnectionUserById(id);
//        return userConnection.getUser1Id() == id ? userConnection.getUser2Id() : userConnection.getUser1Id();
//    }

    @Override
    public boolean isCreated() {
        return true; // Because we don't create a new 'id' for each user, we use in-built userId
    }

    @Override
    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String sqlArgValues() {
        return SQLUtils.TABLE_SIGNATURE_START +
                id +
                SQLUtils.ARGS_SEPARATOR +
                SQLUtils.STRING_QUOTTER +
                city +
                SQLUtils.STRING_QUOTTER +
                SQLUtils.ARGS_SEPARATOR +
                state.ordinal() +
                SQLUtils.ARGS_SEPARATOR +
                connectionId +
                SQLUtils.ARGS_SEPARATOR +
                SQLUtils.STRING_QUOTTER +
                username +
                SQLUtils.STRING_QUOTTER +
                SQLUtils.TABLE_SIGNATURE_END;
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
