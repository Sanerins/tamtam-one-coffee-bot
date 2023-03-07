package one.coffee.sql.user;

import one.coffee.sql.DB;
import one.coffee.sql.Dao;
import one.coffee.sql.tables.Table;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class UserDao
        extends Table
        implements Dao<User> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserDao INSTANCE;

    private UserDao() {
        shortName = "users";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY"),
                Map.entry("city", "VARCHAR(20)"),
                Map.entry("stateId", "INT"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE SET NULL")
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
    public Optional<User> get(long id) throws SQLException {
        AtomicReference<User> user = new AtomicReference<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE id = " + id,
                getInstance().getShortName()
        );
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                LOG.warn("No user with 'id' = {} in DB!", id);
                return;
            }
            user.set(parseUser(rs));
        });
        return Optional.of(user.get());
    }

    @Override
    public List<User> getAll() {
        throw new UnsupportedOperationException("Getting all rows from 'users' is unsupported operation!");
    }

    public List<User> getAllWaitingUsers(long n) {

    }

    @Override
    public void save(User user) {
        DB.putEntity(StaticContext.USERS_TABLE, user);
    }

    @Override
    public void delete(User user) throws SQLException {
        DB.deleteEntity(StaticContext.USERS_TABLE, user);
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
