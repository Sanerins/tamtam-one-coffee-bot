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

public class UsersTable extends Table {

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
        String query = MessageFormat.format("SELECT user1Id, city1, state1Id, connection1Id, user2Id, {0}.city AS city2, {0}.stateId AS state2Id, {1}.id AS connection2Id" +
                " FROM (" +
                    " SELECT {0}.userId AS userId, city, {0}.stateId AS stateId, userConnections.id AS connectionId, user2Id" +
                    " FROM {0}" +
                        " LEFT JOIN {2} ON {2}.stateId = {0}.stateId" +
                        " LEFT JOIN {1} ON {1}.id = {0}.connectionId" +
                        " WHERE {0}.userId = " + id + // Мы должны гарантировать, что у Connection.User2 тип связи будет такой же, как и у User1
                " ) AS first_part " +
                " LEFT JOIN {0} ON {0}.id = user2Id",
                UsersTable.INSTANCE.shortName,
                UserConnectionsTable.INSTANCE.shortName,
                UserStatesTable.INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No user with such id in DB: " + id);
            }

            long user1Id = rs.getLong("user1Id");
            String city1 = rs.getString("city1");
            UserState userState1 = new UserState(
                    UserState.StateType.fromId(rs.getLong("state1Id"))
            );
            User user1 = new User(user1Id, city1, userState1, null);

            long user2Id = rs.getLong("user2Id");
            String city2 = rs.getString("city2");
            UserState userState2 = new UserState(
                    UserState.StateType.fromId(rs.getLong("state2Id"))
            );
            User user2 = new User(user2Id, city2, userState2, null);

            UserConnection userConnection1 = new UserConnection(
                    rs.getLong("connection1Id"),
                    user1,
                    user2
            );
            user1.setUserConnection(userConnection1);

            UserConnection userConnection2 = new UserConnection(
                    rs.getLong("connection2Id"),
                    user2,
                    user1
            );
            user2.setUserConnection(userConnection2);

            // TODO А это вообще возможно?
            if (user1.getUserConnection().getId() != user2.getUserConnection().getId()) {
                LOG.warn("Connection id for user1 = {} and user 2 = {} are not equals! Breaking the link.", user1, user2);

                DB.deleteEntityById(UserConnectionsTable.INSTANCE, userConnection1.getId());
                user1.setState(UserState.DEFAULT);
                DB.putEntity(UsersTable.INSTANCE, user1);

                DB.deleteEntityById(UserConnectionsTable.INSTANCE, userConnection2.getId());
                user2.setState(UserState.DEFAULT);
                DB.putEntity(UsersTable.INSTANCE, user2);
            }

            // TODO А это вообще возможно?
            if (user1.getState() != user2.getState()) {
                LOG.warn("State id for user1 = {} and user 2 = {} are not equals! Revert states to DEFAULT one.", user1, user2);

                user1.setState(UserState.DEFAULT);
                DB.putEntity(UsersTable.INSTANCE, user1);

                user2.setState(UserState.DEFAULT);
                DB.putEntity(UsersTable.INSTANCE, user2);
            }

            user.set(user1);
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
