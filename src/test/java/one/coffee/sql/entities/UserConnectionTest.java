package one.coffee.sql.entities;

import one.coffee.DBTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserConnectionTest {

    @Test
    void ok() {
        final long user1Id = 123;
        final String user1City = "St. Petersburg";
        final UserState user1State = UserState.DEFAULT;
        final UserConnection user1Connection = null;
        User user1 = new User(user1Id, user1City, user1State, user1Connection);

        final long user2Id = 124;
        final String user2City = "St. Petersburg";
        final UserState user2State = UserState.DEFAULT;
        final UserConnection user2Connection = null;
        User user2 = new User(user2Id, user2City, user2State, user2Connection);

        UserConnection userConnection = new UserConnection(user1, user2);
        userConnection.createNominalConnection();

        assertEquals(user1.getUserConnection(), userConnection);
        assertEquals(user2.getUserConnection(), userConnection);
        assertEquals(user1.getConnectedUser(), user2);
        assertEquals(user2.getConnectedUser(), user1);
    }

    @Test
    void invalidUser1() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        User user = new User(userId, userCity, userState, userConnection);

        assertThrows(Exception.class, () -> new UserConnection(null, user));
    }

    @Test
    void invalidUser2() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        User user = new User(userId, userCity, userState, userConnection);

        assertThrows(Exception.class, () -> new UserConnection(user, null));
    }

}
