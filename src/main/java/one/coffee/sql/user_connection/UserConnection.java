package one.coffee.sql.user_connection;

import one.coffee.sql.Entity;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class UserConnection
        implements Entity {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final long user1Id;
    private final long user2Id;
    private long id;

    public UserConnection(long user1Id, long user2Id) {
        this(SQLUtils.NO_ID, user1Id, user2Id);
    }

    public UserConnection(long id, long user1Id, long user2Id) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
    }

    @Override
    public boolean isCreated() {
        return StaticContext.USER_CONNECTION_SERVICE.getByUserId(user1Id).isPresent();
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

    @Override
    public String toString() {
        return "UserConnection{" +
                "id=" + id +
                ", user1Id=" + user1Id +
                ", user2Id=" + user2Id +
                '}';
    }

    @Override
    public String sqlArgValues() {
        StringBuilder sqlValues = new StringBuilder(SQLUtils.TABLE_SIGNATURE_START);

        if (isCreated()) {
            sqlValues.append(id).append(SQLUtils.ARGS_SEPARATOR);
        }

        return sqlValues.append(user1Id).append(SQLUtils.ARGS_SEPARATOR)
                .append(user2Id).append(SQLUtils.TABLE_SIGNATURE_END).toString();
    }

}
