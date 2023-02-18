package one.coffee.sql.tables;

import one.coffee.BaseTest;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserConnectionsTableTest extends BaseTest {

    static {
        UserConnectionsTableTest.table = UserConnectionsTable.INSTANCE;
    }

    @Test
    void ok() {
        final long user1Id = 123;
        final String user1City = "St. Petersburg";
        final UserState user1State = UserState.DEFAULT;
        UserConnection userConnection = null;
        User user1 = new User(user1Id, user1City, user1State, userConnection);

        final long user2Id = 124;
        final String user2City = "St. Petersburg";
        final UserState user2State = UserState.DEFAULT;
        User user2 = new User(user2Id, user2City, user2State, userConnection);

        userConnection = new UserConnection(user1, user2);
        UserConnectionsTable.putUserConnection(userConnection);

        UserConnection savedUserConnection = UserConnectionsTable.getUserConnectionByUserId(user1Id);
        assertEquals(savedUserConnection.getUser1(), user1);
        assertEquals(savedUserConnection.getUser2(), user2);
        assertEquals(savedUserConnection.getId(), userConnection.getId());
    }

    @Test
    void twoParallelConnections() {
        final long user1Id = 123;
        final String user1City = "St. Petersburg";
        final UserState user1State = UserState.DEFAULT;
        UserConnection user1Connection = null;
        User user1 = new User(user1Id, user1City, user1State, user1Connection);

        final long user2Id = 124;
        final String user2City = "St. Petersburg";
        final UserState user2State = UserState.DEFAULT;
        UserConnection user2Connection = null;
        User user2 = new User(user2Id, user2City, user2State, user2Connection);

        final long user3Id = 125;
        final String user3City = "St. Petersburg";
        final UserState user3State = UserState.DEFAULT;
        UserConnection user3Connection = null;
        User user3 = new User(user3Id, user3City, user3State, user3Connection);

        user1Connection = new UserConnection(user1, user2);
        UserConnectionsTable.putUserConnection(user1Connection);

        user2Connection = new UserConnection(user2, user3);
        UserConnection finalUser2Connection = user2Connection;
        assertThrows(Exception.class, () -> UserConnectionsTable.putUserConnection(finalUser2Connection));

        UserConnection savedUserConnection = UserConnectionsTable.getUserConnectionByUserId(user1Id);
        assertEquals(savedUserConnection.getId(), user1Connection.getId());
        assertEquals(savedUserConnection.getUser1(), user1);
        assertEquals(savedUserConnection.getUser2(), user2);
    }

}
