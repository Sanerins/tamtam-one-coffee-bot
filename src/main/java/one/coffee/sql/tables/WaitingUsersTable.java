package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserState;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class WaitingUsersTable
        extends Table {

    public static final WaitingUsersTable INSTANCE = new WaitingUsersTable();

    private WaitingUsersTable() {
        shortName = "waitingUsers";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("userId", "INT REFERENCES users(userId) ON DELETE CASCADE")
        );
        init();
    }

    public static List<User> getWaitingUsers() {
        List<Long> userIds = new ArrayList<>();
        String query = MessageFormat.format(
                "SELECT userId" +
                        " FROM {0}",
                INSTANCE.getShortName()
        );

        DB.executeQuery(query, rs -> {
            while (rs.next()) {
                userIds.add(rs.getLong("userId"));
            }
        });

        return userIds.stream().map(UsersTable::getUserById).toList();
    }

    public static void putWaitingUser(User user) {
        DB.putEntity(INSTANCE, user);
    }

    public static void deleteWaitingUserById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }
}
