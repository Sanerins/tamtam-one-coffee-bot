package one.coffee.sql.user_connection;

import one.coffee.sql.Entity;
import one.coffee.sql.states.UserConnectionState;
import one.coffee.sql.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class UserConnection
        implements Entity {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private long id;
    private long user1Id;
    private long user2Id;
    private boolean approve1;
    private boolean approve2;
    private UserConnectionState state;

    public UserConnection(long user1Id, long user2Id) {
        this(SQLUtils.DEFAULT_ID, user1Id, user2Id, false, false, UserConnectionState.IN_PROGRESS);
    }

    public UserConnection(long id, long user1Id, long user2Id, boolean approve1, boolean approve2, UserConnectionState state) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.approve1 = approve1;
        this.approve2 = approve2;
        this.state = state;
    }

    public boolean isApprove1() {
        return approve1;
    }

    public boolean isApprove2() {
        return approve2;
    }

    public boolean isAllApprove() {
        return isApprove1() && isApprove2();
    }

    @Override
    public boolean isCreated() {
        return id != SQLUtils.DEFAULT_ID;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser1Id(long user1Id) {
        this.user1Id = user1Id;
    }

    public void setUser2Id(long user2Id) {
        this.user2Id = user2Id;
    }

    public long getUser1Id() {
        return user1Id;
    }

    public long getUser2Id() {
        return user2Id;
    }

    public void setApprove1(boolean approve1) {
        this.approve1 = approve1;
    }

    public void setApprove2(boolean approve2) {
        this.approve2 = approve2;
    }

    public UserConnectionState getState() {
        return state;
    }

    public void setState(UserConnectionState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "UserConnection{" +
                "id=" + id +
                ", user1Id=" + user1Id +
                ", user2Id=" + user2Id +
                ", approve1=" + approve1 +
                ", approve2=" + approve2 +
                ", state=" + state +
                '}';
    }

    @Override
    public String sqlArgValues() {
        StringBuilder sqlValues = new StringBuilder();
        sqlValues.append(SQLUtils.TABLE_SIGNATURE_START);
        if (isCreated()) {
            sqlValues
                    .append(id)
                    .append(SQLUtils.ARGS_SEPARATOR);
        }
        return sqlValues
                .append(user1Id)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(user2Id)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(approve1)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(approve2)
                .append(SQLUtils.ARGS_SEPARATOR)
                .append(state.ordinal())
                .append(SQLUtils.TABLE_SIGNATURE_END)
                .toString();
    }
}
