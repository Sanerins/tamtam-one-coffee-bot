package one.coffee.sql.entities;

import one.coffee.DBTest;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserConnectionTest {

    @DBTest(nUsers = 2)
    void ok(List<User> users) throws SQLException {
        User user1 = users.get(0);
        User user2 = users.get(1);

        UserConnection userConnection = new UserConnection(user1, user2);

        assertEquals(user1.getConnectionId(), userConnection.getId());
        assertEquals(user2.getConnectionId(), userConnection.getId());

        assertEquals(user1.getStateId(), UserState.CHATTING.getStateId());
        assertEquals(user2.getStateId(), UserState.CHATTING.getStateId());

        assertEquals(user1.getConnectedUserId(), user2.getUserId());
        assertEquals(user2.getConnectedUserId(), user1.getUserId());
    }

    @Test
    void invalidUser1() throws SQLException {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        User user = new User(userId, userCity, stateId, connectionId);
        user.commit();

        assertThrows(Exception.class, () -> new UserConnection(-1, userId));
    }

    @Test
    void invalidUser2() throws SQLException {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        User user = new User(userId, userCity, stateId, connectionId);
        user.commit();

        assertThrows(Exception.class, () -> new UserConnection(userId, -1));
    }

}
