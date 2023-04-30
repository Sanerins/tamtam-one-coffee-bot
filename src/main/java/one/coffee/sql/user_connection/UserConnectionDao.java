package one.coffee.sql.user_connection;

import one.coffee.sql.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class UserConnectionDao
        extends Dao<UserConnection> {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public UserConnectionDao() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("approve1", "BIT"),
                Map.entry("approve2", "BIT")
        );
    }

    @Override
    public Optional<UserConnection> get(long id) {
        final AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        final String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE id = " + id,
                this.getShortName());
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                return;
            }
            final long actualUser1Id = rs.getLong("user1Id");
            final long actualUser2Id = rs.getLong("user2Id");
            final boolean approve1 = rs.getBoolean("approve1");
            final boolean approve2 = rs.getBoolean("approve2");
            userConnection.set(new UserConnection(id, actualUser1Id, actualUser2Id, approve1, approve2));
        });
        return Optional.ofNullable(userConnection.get());
    }

    public Optional<UserConnection> getByUserId(long userId) {
        final AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        final String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE user1Id = " + userId + " OR user2Id = " + userId,
                this.getShortName());
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                return;
            }
            final long id = rs.getLong("id");
            final long actualUser1Id = rs.getLong("user1Id");
            final long actualUser2Id = rs.getLong("user2Id");
            final boolean approve1 = rs.getBoolean("approve1");
            final boolean approve2 = rs.getBoolean("approve2");
            userConnection.set(new UserConnection(id, actualUser1Id, actualUser2Id, approve1, approve2));
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
