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

    public static UserConnection getUserConnectionById(long id) {
        AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        String query = MessageFormat.format(
                "SELECT * FROM {0}",
                INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No userConnection with such id in DB: " + id);
            }

            long userConnectionId = rs.getLong("id");



            UserState.StateType stateType = UserState.StateType.fromId(rs.getLong("stateId"));
            userState.set(new UserState(stateType));
        });

        return userState.get();
    }

    public static void putUserConnection(UserConnection userConnection) {
        DB.putEntity(INSTANCE, userConnection);
    }

    public static void deleteUserConnectionById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }
}
