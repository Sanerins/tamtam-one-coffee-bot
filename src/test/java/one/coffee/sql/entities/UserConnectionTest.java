package one.coffee.sql.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserConnectionTest {

    @Test
    void ok() {
        final long user1Id = 123;
        final String user1City = "St. Petersburg";
        final long state1Id = UserState.DEFAULT.getStateId();
        final long connection1Id = -1;
        User user1 = new User(user1Id, user1City, state1Id, connection1Id);

        final long user2Id = 124;
        final String user2City = "St. Petersburg";
        final long state2Id = UserState.DEFAULT.getStateId();
        final long connection2Id = -1;
        User user2 = new User(user2Id, user2City, state2Id, connection2Id);

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnection.commit();

        assertEquals(user1.getConnectionId(), userConnection.getId());
        assertEquals(user2.getConnectionId(), userConnection.getId());

        assertEquals(user1.getStateId(), UserState.CHATTING.getStateId());
        assertEquals(user2.getStateId(), UserState.CHATTING.getStateId());

        assertEquals(user1.getConnectedUserId(), user2.getId());
        assertEquals(user2.getConnectedUserId(), user1.getId());
    }

    @Test
    void invalidUser1() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        User user = new User(userId, userCity, stateId, connectionId);
        user.commit();

        assertThrows(Exception.class, () -> new UserConnection(-1, userId));
    }

    @Test
    void invalidUser2() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        User user = new User(userId, userCity, stateId, connectionId);
        user.commit();

        assertThrows(Exception.class, () -> new UserConnection(userId, -1));
    }

}
