package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.UserConnection;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class UserConnectionsTable
        extends Table {

    public static final UserConnectionsTable INSTANCE = new UserConnectionsTable();

    private UserConnectionsTable() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE")
        );
        init();
    }

    public static UserConnection getUserConnectionByUserId(long userId) {
        AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE user1Id = " + userId + " OR user2Id = " + userId,
                UserConnectionsTable.INSTANCE.getShortName());
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("User with 'userId' = " + userId + " has not any connections!");
            }
            long id = rs.getLong("id");
            long user1Id = rs.getLong("user1Id");
            long user2Id = rs.getLong("user2Id");
            userConnection.set(new UserConnection(id, user1Id, user2Id));
        });
        return userConnection.get();
    }

    public static void putUserConnection(UserConnection userConnection) {
        DB.putEntity(INSTANCE, userConnection);
    }

    public static void deleteUserConnection(UserConnection userConnection) {
        DB.deleteEntity(INSTANCE, userConnection);
    }

}
