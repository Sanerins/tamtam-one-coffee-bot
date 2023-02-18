package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WaitingUsersTable extends Table {

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
        return null;
    }

    public static void putWaitingUser(User user) {
        DB.putEntity(INSTANCE, user);
    }

    public static void deleteWaitingUserById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }
}
