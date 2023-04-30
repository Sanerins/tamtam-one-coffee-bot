package one.coffee.sql.user_connection;

import java.lang.invoke.MethodHandles;

import one.coffee.sql.Entity;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.sql.utils.UserConnectionState;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserConnection implements Entity {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserConnectionService userConnectionService = StaticContext.USER_CONNECTION_SERVICE;

    private final long user1Id;
    private final long user2Id;
    private final long id;
    private boolean approve1;
    private boolean approve2;
    private UserConnectionState state;

    public UserConnection(long user1Id, long user2Id) {
        this(user1Id, user2Id, false, false);
    }

    public UserConnection(long user1Id, long user2Id, boolean approve1, boolean approve2) {
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
        return userConnectionService.getInProgressConnectionByUserId(user1Id).isPresent();
    }

    @Override
    public long getId() {
        return id;
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
                "user1Id=" + user1Id +
                ", user2Id=" + user2Id +
                ", id=" + id +
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
                .append(state)
                .append(SQLUtils.TABLE_SIGNATURE_END)
                .toString();
    }
}
