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

public class UsersTable extends Table {

    public static final UsersTable INSTANCE = new UsersTable();

    private UsersTable() {
        shortName = "users";
        // Здесь и во всех остальных табличках заюзана LinkedHashMap поверх обычной мапы для сохранения порядка вставки,
        // поскольку он нам важен при переводе сигнатур и тел табличек в строку

        args = List.of(
                // TODO У SQLite есть проблема, что AUTOINCREMENT действует только на тип INTEGER, а нам хотелось бы тип пошире
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                // TODO Решить проблему составного ключа, так как и id, и userId должны быть уникальны
                Map.entry("userId", "BIGINT"),
                Map.entry("city", "VARCHAR(20)"),
                Map.entry("stateId", "INT REFERENCES states(stateId)"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id)")
        );
        init();
    }

    public static User getUserById(long id) {
        AtomicReference<User> user = new AtomicReference<>();
        String query = MessageFormat.format(
                "SELECT {0}.userId AS userId, city, {0}.stateId AS stateId, stateName, {1}.id as connectionId, user1Id, user2Id" +
                " FROM {0}" +
                        " JOIN {2} ON {2}.stateId = {0}.stateId" +
                        " JOIN {1} ON {1}.id = {0}.connectionId" +
                        " WHERE {0}.userId = {3}",
                INSTANCE.shortName,
                UserConnectionsTable.INSTANCE.shortName,
                StatesTable.INSTANCE.shortName,
                id);

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No user with such id in DB: " + id);
            }

            long userId = rs.getLong("userId");
            String city = rs.getString("city");
            UserState userState = new UserState(
                    rs.getLong("stateId"),
                    rs.getString("stateName")
            );
            UserConnection userConnection = new UserConnection(
                    rs.getLong("connectionId"),
                    rs.getLong("user1Id"),
                    rs.getLong("user2Id")
            );

            user.set(new User(userId, city, userState, userConnection));
        });

        return user.get();
    }

    public static void putUser(User user) {
        String query = "INSERT OR REPLACE INTO " + INSTANCE.signature() + " VALUES " + user.sqlValues();
        DB.executeQuery(query);
    }

    public static void deleteUserById(long id) {
        // ON DELETE CASCADE
    }
}
