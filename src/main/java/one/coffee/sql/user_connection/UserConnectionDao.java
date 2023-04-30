package one.coffee.sql.user_connection;

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
import one.coffee.sql.utils.UserConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserConnectionDao
        extends Dao<UserConnection>
{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserConnectionDao INSTANCE;

    private UserConnectionDao() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("approve1", "BIT"),
                Map.entry("approve2", "BIT")
        );
        init();
    }

    public static UserConnectionDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserConnectionDao();
        }
        return INSTANCE;
    }

    @Override
    public Optional<UserConnection> get(long id) {
        final AtomicReference<UserConnection> userConnection = new AtomicReference<>();
        final String query = MessageFormat.format("""
                        SELECT *
                        FROM {0}
                        WHERE id = {1}
                        """, getInstance().getShortName(), id);
        DB.executeQueryWithActionForResult(query, rs -> {
            if (!rs.next()) {
                return;
            }
            userConnection.set(parseUserConnection(rs));
        });
        return Optional.ofNullable(userConnection.get());
    }



    public List<UserConnection> getByUserId(long userId) {
        final List<UserConnection> userConnections = new ArrayList<>();
        final String query = MessageFormat.format("""
                SELECT *
                FROM {0}
                WHERE user1Id = {1} OR user2Id = {1}
                """, getInstance().getShortName(), userId);
        DB.executeQueryWithActionForResult(query, rs -> {
            while (rs.next()) {
                userConnections.add(parseUserConnection(rs));
            }
        });
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
