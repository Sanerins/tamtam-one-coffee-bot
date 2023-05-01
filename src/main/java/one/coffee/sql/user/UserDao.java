package one.coffee.sql.user;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import one.coffee.sql.DB;
import one.coffee.sql.Dao;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao extends Dao<User> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserDao INSTANCE;

    private UserDao() {
        super(
                "users",
                List.of(
                        Map.entry("id", "INTEGER PRIMARY KEY"),
                        Map.entry("city", "VARCHAR(20)"),
                        Map.entry("stateId", "INT"),
                        Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE SET NULL"),
                        Map.entry("username", "VARCHAR(64)"),
                        Map.entry("userInfo", "TEXT")
                )
        );
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
        try {
            String sql = "SELECT * FROM " + getInstance().getShortName() + " WHERE id = ?";
            PreparedStatement stmt = StaticContext.CON.prepareStatement(sql);
            stmt.setLong(1, id);
            DB.executeQuery(stmt, rs -> {
                if (!rs.next()) {
                    return;
                }
                user.set(parseUser(rs));
            });
        } catch (SQLException e) {
            LOG.warn("When getting user", e);
        }
        return Optional.ofNullable(user.get());
    }

    public List<User> getWaitingUsers(long n) {
        List<User> users = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + getInstance().getShortName() +  " WHERE stateId = ? ORDER BY RANDOM() LIMIT " + n;
            PreparedStatement stmt = StaticContext.CON.prepareStatement(sql);
            stmt.setLong(1, UserState.WAITING.ordinal());

            DB.executeQuery(stmt, rs -> {
                while (rs.next()) {
                    users.add(parseUser(rs));
                }
            });
        } catch (SQLException e) {
            LOG.warn("When getting waiting users", e);
        }
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
