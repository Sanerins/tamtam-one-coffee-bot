package one.coffee.sql.user_connection;

import one.coffee.sql.Dao;
import one.coffee.sql.states.UserConnectionState;
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
public class UserConnectionDao
        extends Dao<UserConnection> {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public UserConnectionDao() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("approve1", "BIT"),
                Map.entry("approve2", "BIT"),
                Map.entry("stateId", "INT")
        );
    }

    @Override
    public Optional<UserConnection> get(long id) {
        final AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        try {
            String sql = "SELECT * FROM " + this.getShortName()+ " WHERE id = ?";
            PreparedStatement stmt = DB.prepareStatement(sql);
            stmt.setLong(1, id);

            DB.executeQuery(stmt, rs -> {
                if (!rs.next()) {
                    return;
                }
                userConnection.set(parseUserConnection(rs));
            });
        } catch (SQLException e) {
            LOG.error("Error when getting user connection", e);
        }
        return Optional.ofNullable(userConnection.get());
    }

    public List<UserConnection> getByUserId(long userId) {
        final List<UserConnection> userConnections = new ArrayList<>();
        try {
            String sql = "SELECT * FROM " + this.getShortName() + " WHERE user1Id = ? OR user2Id = ?";
            PreparedStatement stmt = DB.prepareStatement(sql);
            stmt.setLong(1, userId);
            stmt.setLong(2, userId);

            DB.executeQuery(stmt, rs -> {
                while (rs.next()) {
                    userConnections.add(parseUserConnection(rs));
                }
            });
        } catch (SQLException e) {
            LOG.error("Error when getting user connections", e);
        }
        return userConnections;
    }

    @Override
    public void save(UserConnection userConnection) {
        DB.putEntity(this, userConnection);
    }

    @Override
    public void delete(UserConnection userConnection) {
        DB.deleteEntity(this, userConnection);
    }

    private static UserConnection parseUserConnection(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long user1Id = rs.getLong("user1Id");
        long user2Id = rs.getLong("user2Id");
        boolean approve1 = rs.getBoolean("approve1");
        boolean approve2 = rs.getBoolean("approve2");
        UserConnectionState state = UserConnectionState.fromId(rs.getLong("stateId"));
        return new UserConnection(id, user1Id, user2Id, approve1, approve2, state);
    }
}
