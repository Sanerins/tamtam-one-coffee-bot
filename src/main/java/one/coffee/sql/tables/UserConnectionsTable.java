package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.UserConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class UserConnectionsTable
        extends Table {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserConnectionsTable INSTANCE;

    private UserConnectionsTable() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE")
        );
        init();
    }

    public static UserConnectionsTable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserConnectionsTable();
        }
        return INSTANCE;
    }

    public static UserConnection getUserConnectionUserById(long userId) throws SQLException {
        AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE user1Id = " + userId + " OR user2Id = " + userId,
                getInstance().getShortName());
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                LOG.warn("User with 'id' = {} has not any connections!", userId);
                return;
            }
            long id = rs.getLong("id");
            long user1Id = rs.getLong("user1Id");
            long user2Id = rs.getLong("user2Id");
            userConnection.set(new UserConnection(id, user1Id, user2Id));
        });
        return userConnection.get();
    }

    public static void putUserConnection(UserConnection userConnection) {
        DB.putEntity(getInstance(), userConnection);
    }

    public static void deleteUserConnection(UserConnection userConnection) throws SQLException {
        DB.deleteEntity(getInstance(), userConnection);
    }

}
