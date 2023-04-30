package one.coffee.sql.user;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import one.coffee.sql.DB;
import one.coffee.sql.Dao;
import one.coffee.sql.utils.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao extends Dao<User> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserDao INSTANCE;

    private UserDao() {
        shortName = "users";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY"),
                Map.entry("city", "VARCHAR(20)"),
                Map.entry("stateId", "INT"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE SET NULL"),
                Map.entry("username", "VARCHAR(64)"),
                Map.entry("userInfo", "TEXT")
        );
        init();
    }

    public static UserDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserDao();
        }
        return INSTANCE;
    }

    @Override
    public Optional<User> get(long id) {
        AtomicReference<User> user = new AtomicReference<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE id = " + id,
                getInstance().getShortName()
        );
        DB.executeQueryWithActionForResult(query, rs -> {
            if (!rs.next()) {
                return;
            }
            user.set(parseUser(rs));
        });
        return Optional.ofNullable(user.get());
    }

    public List<User> getWaitingUsers(long n) {
        List<User> users = new ArrayList<>();
        String query = MessageFormat.format("""
                        SELECT *
                        FROM {0}
                        WHERE stateId = {1}
                        ORDER BY RANDOM()
                        LIMIT {2}
                        """, getInstance().getShortName(), UserState.WAITING.ordinal(), n);

        DB.executeQueryWithActionForResult(query, rs -> {
            while (rs.next()) {
                users.add(parseUser(rs));
            }
        });

        return users;
    }

    @Override
    public void save(User user) {
        DB.putEntity(this, user);
    }

    @Override
    public void delete(User user) {
        DB.deleteEntity(this, user);
    }

    private static User parseUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("city"),
                UserState.fromId(rs.getLong("stateId")),
                rs.getLong("connectionId"),
                rs.getString("username"),
                rs.getString("userInfo")
        );
    }

}
