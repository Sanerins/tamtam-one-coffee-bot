package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.utils.StaticContext;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserConnectionServiceTest
        extends ResourceTest {

    private static final UserConnectionService userConnectionService = StaticContext.USER_CONNECTION_SERVICE;
    private static final UserService userService = StaticContext.USER_SERVICE;

    @DBTest(nUsers = 2)
    void ok(List<User> users) {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnectionService.save(userConnection);

        UserConnection savedUserConnection = userConnectionService.getByUserId(user1Id).get();
        User savedUser1 = userService.get(user1Id).get();
        User savedUser2 = userService.get(user2Id).get();

        assertEquals(savedUser1.getState(), UserState.CHATTING);
        assertEquals(savedUserConnection.getId(), savedUser1.getConnectionId());

        assertEquals(savedUser2.getState(), UserState.CHATTING);
        assertEquals(savedUserConnection.getId(), savedUser2.getConnectionId());
    }

    @DBTest(nUsers = 3)
    void twoParallelConnections(List<User> users) {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();
        long user3Id = users.get(2).getId();

        UserConnection users12Connection = new UserConnection(user1Id, user2Id);
        userConnectionService.save(users12Connection);

        UserConnection users23Connection = new UserConnection(user2Id, user3Id);
        userConnectionService.save(users23Connection);

        User savedUser1 = userService.get(user1Id).get();
        User savedUser2 = userService.get(user2Id).get();
        User savedUser3 = userService.get(user3Id).get();

        UserConnection savedUsers12Connection = userConnectionService.getByUserId(user1Id).get();

        assertTrue(userConnectionService.getByUserId(user3Id).isEmpty());

        assertEquals(savedUser1.getState(), UserState.CHATTING);
        assertEquals(savedUser1.getConnectionId(), savedUsers12Connection.getId());

        assertEquals(savedUser2.getState(), UserState.CHATTING);
        assertEquals(savedUser2.getConnectionId(), savedUsers12Connection.getId());

        assertNotEquals(savedUser3.getState(), UserState.CHATTING);
        assertEquals(savedUser3.getConnectionId(), SQLUtils.NO_ID);
    }

    @DBTest(nUsers = 2)
    void breakConnection(List<User> users) {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnectionService.save(userConnection);
        userConnectionService.delete(userConnection);

        User savedUser1 = userService.get(user1Id).get();
        User savedUser2 = userService.get(user2Id).get();

        assertEquals(savedUser1.getState(), UserState.WAITING);
        assertEquals(savedUser1.getConnectionId(), -1);

        assertEquals(savedUser2.getState(), UserState.WAITING);
        assertEquals(savedUser2.getConnectionId(), -1);
    }

}
