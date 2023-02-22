package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class UsersTable
        extends Table {

    public static final UsersTable INSTANCE = new UsersTable();
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private UsersTable() {
        shortName = "users";
        args = List.of(
                Map.entry("userId", "INTEGER PRIMARY KEY"),
                Map.entry("city", "VARCHAR(20)"),
                Map.entry("stateId", "INT REFERENCES states(stateId) ON DELETE SET NULL"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE SET NULL")
        );
        init();
    }

    public static User getUserByUserId(long userId) throws SQLException {
        AtomicReference<User> user = new AtomicReference<>();
        // TODO Этот скрипт будет изменён в будущем. См. комментарий в UserConnectionsTable(line 59).
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE userId = " + userId,
                UsersTable.INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No user with 'userId' = " + userId + " in DB!");
            }

            user.set(parseUser(rs));
        });

        return user.get();
    }

    public static List<User> getAllUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        String query = MessageFormat.format("SELECT * FROM {0}",
                UsersTable.INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            while (rs.next()) {
                User user = UsersTable.parseUser(rs);
                allUsers.add(user);
            }
        });

        return allUsers;
    }

    public static void putUser(User user) {
        DB.putEntity(INSTANCE, user);
    }

    public static void deleteUser(User user) throws SQLException {
        DB.deleteEntity(INSTANCE, user);
    }

    public static List<User> getWaitingUsers() throws SQLException {
        return getWaitingUsers(Integer.MAX_VALUE);
    }

    public static List<User> getWaitingUsers(long limit) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE stateId = " + UserState.WAITING.getStateId() +
                        " LIMIT " + limit,
                UsersTable.INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            while (rs.next()) {
                users.add(parseUser(rs));
            }
        });

        return users;
    }

    private static User parseUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("userId"),
                rs.getString("city"),
                rs.getLong("stateId"),
                rs.getLong("connectionId")
        );
    }

}
