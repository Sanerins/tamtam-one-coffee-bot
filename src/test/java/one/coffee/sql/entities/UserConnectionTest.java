package one.coffee.sql.entities;

import one.coffee.DBTest;
import one.coffee.sql.user.UsersTable;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserConnectionTest {

    @DBTest(nUsers = 2)
    void ok(List<User> users) throws SQLException {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnection.commit();

        User user1 = UsersTable.getUserByUserId(user1Id);
        User user2 = UsersTable.getUserByUserId(user2Id);

        assertEquals(user1.getConnectionId(), userConnection.getId());
        assertEquals(user2.getConnectionId(), userConnection.getId());

        assertEquals(user1.getState(), UserState.CHATTING);
        assertEquals(user1.getState(), UserState.CHATTING);

        assertEquals(user1.getConnectedUserId(), user2.getId());
        assertEquals(user2.getConnectedUserId(), user1.getId());
    }

    @Test
    void invalidUser1() throws SQLException {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId);
        user.commit();

        assertThrows(Exception.class, () -> new UserConnection(-1, userId));
    }

    @Test
    void invalidUser2() throws SQLException {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId);
        user.commit();

        assertThrows(Exception.class, () -> new UserConnection(userId, -1));
    }

}
