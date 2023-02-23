package one.coffee.sql.tables;

import one.coffee.DBTest;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;

import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserConnectionsTableTest
        extends TableTest {

    @DBTest(nUsers = 2)
    void ok(List<User> users) throws SQLException {
        User user1 = users.get(0);
        User user2 = users.get(1);

        UserConnection userConnection = new UserConnection(user1.getId(), user2.getId());
        userConnection.commit();
        UserConnection savedUserConnection = UserConnectionsTable.getUserConnectionUserById(user1.getId());

        assertEquals(savedUserConnection.getId(), userConnection.getId());
        assertEquals(savedUserConnection.getUser1Id(), user1.getId());
        assertEquals(savedUserConnection.getUser2Id(), user2.getId());
    }

    @DBTest(nUsers = 3)
    void twoParallelConnections(List<User> users) throws SQLException {
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        UserConnection user1Connection = new UserConnection(user1.getId(), user2.getId());
        user1Connection.commit();

        assertThrows(Exception.class, () -> new UserConnection(user2.getId(), user3.getId()));

        UserConnection savedUserConnection = UserConnectionsTable.getUserConnectionUserById(user1.getId());

        assertEquals(savedUserConnection.getId(), user1Connection.getId());
        assertEquals(savedUserConnection.getUser1Id(), user1.getId());
        assertEquals(savedUserConnection.getUser2Id(), user2.getId());
    }

    @DBTest(nUsers = 2)
    void breakConnection(List<User> users) throws SQLException {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnection.commit();
        userConnection.breakConnection();

        User user1 = UsersTable.getUserByUserId(user1Id);
        User user2 = UsersTable.getUserByUserId(user2Id);

        assertEquals(-1, user1.getConnectionId());
        assertEquals(-1, user2.getConnectionId());

        assertEquals(user1.getState(), UserState.DEFAULT);
        assertEquals(user2.getState(), UserState.DEFAULT);
    }

}
