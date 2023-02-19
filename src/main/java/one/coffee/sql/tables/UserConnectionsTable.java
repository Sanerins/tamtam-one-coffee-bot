package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

    public static List<UserConnection> getUserConnectionsByUsersId(long userId) {
        List<UserConnection> userConnections = new ArrayList<>();
        String query = MessageFormat.format(
                "SELECT *" +
                        " FROM {0}" +
                        " WHERE userId = ",
                INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No UserConnection for 'userId'=" + userId);
            }

            long userConnectionId = rs.getLong("id");

            long user1Id = rs.getLong("user1Id");
            String city1 = rs.getString("city1");
            UserState user1State = new UserState(UserState.StateType.fromId(rs.getLong("state1Id")));
            User user1 = new User(user1Id, city1, user1State, null);

            long user2Id = rs.getLong("user2Id");
            String city2 = rs.getString("city2");
            UserState user2State = new UserState(UserState.StateType.fromId(rs.getLong("state2Id")));
            User user2 = new User(user2Id, city2, user2State, null);

            userConnections.add(new UserConnection(userConnectionId, user1, user2));
        });

        return userConnections;
    }

    public static void putUserConnection(UserConnection userConnection) {
        DB.putEntity(INSTANCE, userConnection);
    }

    public static void deleteUserConnectionById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }

}
