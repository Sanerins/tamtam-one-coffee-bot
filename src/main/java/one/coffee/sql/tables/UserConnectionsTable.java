package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class UserConnectionsTable extends Table {

    public static final UserConnectionsTable INSTANCE = new UserConnectionsTable();

    private UserConnectionsTable() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "BIGINT PRIMARY KEY"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE")
        );
        init();
    }

    public static UserConnection getUserConnectionByUserId(long userId) {
        AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        String query = MessageFormat.format(
                "SELECT *" +
                        " FROM {0}" +
                        " WHERE user1Id = " + userId + " OR user2Id = " + userId,
                INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No userConnection with such userId in DB: " + userId);
            }

            long userConnectionId = rs.getLong("id");

            long user1Id = rs.getLong("user1Id");
            String city1 = rs.getString("city1");
            User user1 = new User(user1Id, city1, UserState.CHATTING, null);

            long user2Id = rs.getLong("user2Id");
            String city2 = rs.getString("city2");
            User user2 = new User(user2Id, city2, UserState.CHATTING, null);

            userConnection.set(new UserConnection(user1, user2));
            user1.setUserConnection(userConnection.get());
            user2.setUserConnection(userConnection.get());
        });

        return userConnection.get();
    }

    public static void putUserConnection(UserConnection userConnection) {
        DB.putEntity(INSTANCE, userConnection);
    }

    public static void deleteUserConnectionById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }
}
