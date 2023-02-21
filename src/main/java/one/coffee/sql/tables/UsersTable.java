package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class UsersTable
        extends Table {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final UsersTable INSTANCE = new UsersTable();

    private UsersTable() {
        shortName = "users";
        args = List.of(
                // TODO У SQLite есть проблема, что AUTOINCREMENT действует только на тип INTEGER, а нам хотелось бы тип пошире (или не хотелось бы? :) )
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                // TODO Решить проблему составного ключа, так как и id, и userId должны быть уникальны
                Map.entry("userId", "BIGINT"),
                Map.entry("city", "VARCHAR(20)"),
                Map.entry("stateId", "INT REFERENCES states(stateId) ON DELETE SET NULL"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE SET NULL")
        );
        init();
    }

    public static User getUserById(long id) {
        AtomicReference<User> user = new AtomicReference<>();
        // TODO Этот скрипт будет изменён в будущем. См. комментарий в UserConnectionsTable(line 59).
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE userId = " + id,
                UsersTable.INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No user with such id in DB: " + id);
            }

            user.set(new User(
                    rs.getLong("userId"),
                    rs.getString("city"),
                    rs.getLong("stateId"),
                    rs.getLong("connectionId")
            ));
        });

        return user.get();
    }

    public static void putUser(User user) {
        DB.putEntity(INSTANCE, user);
    }

    public static void deleteUserById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }
}
