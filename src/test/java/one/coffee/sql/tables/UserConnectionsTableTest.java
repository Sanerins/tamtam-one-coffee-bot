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

        UserConnection userConnection = new UserConnection(user1.getUserId(), user2.getUserId());
        UserConnection savedUserConnection = UserConnectionsTable.getUserConnectionByUserId(user1.getUserId());

        assertEquals(savedUserConnection.getId(), userConnection.getId());
        assertEquals(savedUserConnection.getUser1Id(), user1.getUserId());
        assertEquals(savedUserConnection.getUser2Id(), user2.getUserId());
    }

    @DBTest(nUsers = 3)
    void twoParallelConnections(List<User> users) throws SQLException {
        User user1 = users.get(0);
        User user2 = users.get(1);
        User user3 = users.get(2);

        UserConnection user1Connection = new UserConnection(user1.getUserId(), user2.getUserId());

        assertThrows(Exception.class, () -> new UserConnection(user2.getUserId(), user3.getUserId()));

        UserConnection savedUserConnection = UserConnectionsTable.getUserConnectionByUserId(user1.getUserId());

        assertEquals(savedUserConnection.getId(), user1Connection.getId());
        assertEquals(savedUserConnection.getUser1Id(), user1.getUserId());
        assertEquals(savedUserConnection.getUser2Id(), user2.getUserId());
    }

    @DBTest(nUsers = 2)
    void breakConnection(List<User> users) throws SQLException {
        User user1 = users.get(0);
        User user2 = users.get(1);

        UserConnection userConnection = new UserConnection(user1, user2);
        userConnection.breakConnection(user1, user2);

        assertEquals(-1, user1.getConnectionId());
        assertEquals(-1, user2.getConnectionId());

        assertEquals(user1.getStateId(), UserState.DEFAULT.getId());
        assertEquals(user2.getStateId(), UserState.DEFAULT.getId());
    }

}
