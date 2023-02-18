package one.coffee.sql.tables;

import one.coffee.sql.entities.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WaitingUsersTable extends Table {

    private WaitingUsersTable() {
        shortName = "waitingUsers";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("userId", "INT REFERENCES users(userId) ON DELETE CASCADE")
        );
        INSTANCE = new WaitingUsersTable();
        init();
    }

    public static List<User> getWaitingUsers() {
        return null;
    }

    public static void putWaitingUser(User user) {
        putEntity(user);
    }

    public static void deleteWaitingUserById(long id) {
        deleteEntityById(id);
    }
}
