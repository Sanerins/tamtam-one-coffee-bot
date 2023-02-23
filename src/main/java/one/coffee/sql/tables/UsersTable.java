package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserState;
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

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UsersTable INSTANCE;

    private UsersTable() {
        shortName = "users";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY"),
                Map.entry("city", "VARCHAR(20)"),
                Map.entry("stateId", "INT"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE SET NULL")
        );
        init();
    }

    public static UsersTable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UsersTable();
        }
        return INSTANCE;
    }

    public static User getUserByUserId(long userId) throws SQLException {
        AtomicReference<User> user = new AtomicReference<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE id = " + userId,
                getInstance().getShortName()
        );
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                LOG.warn("No user with 'id' = {} in DB!", userId);
                return;
            }
            user.set(parseUser(rs));
        });
        return user.get();
    }

    public static void putUser(User user) {
        DB.putEntity(getInstance(), user);
    }

    public static void deleteUser(User user) throws SQLException {
        DB.deleteEntity(getInstance(), user);
    }

    public static List<User> getWaitingUsers(long limit) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE stateId = " + UserState.WAITING.ordinal() +
                        " LIMIT " + limit,
                getInstance().getShortName()
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
                rs.getLong("id"),
                rs.getString("city"),
                rs.getLong("stateId"),
                rs.getLong("connectionId")
        );
    }

}
