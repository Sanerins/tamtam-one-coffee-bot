package one.coffee.sql.user;

import one.coffee.sql.Dao;
import one.coffee.sql.states.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class UserDao extends Dao<User> {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public UserDao() {
        shortName = "users";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY"),
                Map.entry("city", "VARCHAR(20)"),
                Map.entry("stateId", "INT"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE SET NULL"),
                Map.entry("username", "VARCHAR(64)"),
                Map.entry("userInfo", "TEXT")
        );
    }

    @Override
    public Optional<User> get(long id) {
        AtomicReference<User> user = new AtomicReference<>();
        try {
            String sql = "SELECT * FROM " + this.getShortName() + " WHERE id = ?";
            PreparedStatement stmt = DB.prepareStatement(sql);
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
            String sql = "SELECT * FROM " + this.getShortName() + " WHERE stateId = ? ORDER BY RANDOM() LIMIT " + n;
            PreparedStatement stmt = DB.prepareStatement(sql);
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
