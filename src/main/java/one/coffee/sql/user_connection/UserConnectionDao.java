package one.coffee.sql.user_connection;

import one.coffee.sql.DB;
import one.coffee.sql.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class UserConnectionDao
        extends Dao<UserConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserConnectionDao INSTANCE;

    private UserConnectionDao() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE")
        );
        init();
    }

    public static UserConnectionDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserConnectionDao();
        }
        return INSTANCE;
    }

    @Override
    public Optional<UserConnection> get(long id) {
        throw new UnsupportedOperationException("Still there is no support for indexing of connections!");
    }

    public Optional<UserConnection> getByUserId(long userId) {
        AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE user1Id = " + userId + " OR user2Id = " + userId,
                getInstance().getShortName());
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                return;
            }
            long id = rs.getLong("id");
            long actualUser1Id = rs.getLong("user1Id");
            long actualUser2Id = rs.getLong("user2Id");
            userConnection.set(new UserConnection(id, actualUser1Id, actualUser2Id));
        });
        return Optional.ofNullable(userConnection.get());
    }

    @Override
    public void save(UserConnection userConnection) {
        DB.putEntity(this, userConnection);
    }

    @Override
    public void delete(UserConnection userConnection) {
        DB.deleteEntity(this, userConnection);
    }

}
