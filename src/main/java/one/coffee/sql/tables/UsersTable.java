package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class UsersTable extends Table {

    public static final UsersTable INSTANCE = new UsersTable();

    private UsersTable() {
        shortName = "users";
        args = Map.of(
                "id", "BIGINT PRIMARY KEY",
                "userId", "BIGINT PRIMARY KEY",
                "stateId", "INT REFERENCES states(stateId)",
                "connectionId", "INT REFERENCES userConnections(id)",
                "city", "VARCHAR(20)"
        );
        init();
    }

    public static User getUserById(long id) {
        AtomicReference<User> user = new AtomicReference<>();
        String query = "SELECT * FROM " + UsersTable.TABLE_NAME + " WHERE id = " + id;
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No user with such id in DB: " + id);
            }

            long userId = rs.getLong("userId");
            UserState userState = new UserState(
                    rs.getLong("stateId"),
                    rs.getString("stateName")
            );
            UserConnection userConnection = new UserConnection(
                    rs.getLong("connectionId"),
                    rs.getLong("user1Id"),
                    rs.getLong("user2Id")
            );
            String city = rs.getString("city");

            user.set(new User(userId, userState, userConnection, city));
        });

        return user.get();
    }

    public static void putUser(User user) {
        String query = "INSERT OR REPLACE INTO " + UsersTable.TABLE_NAME + " WHERE id = " + id;
    }
}
