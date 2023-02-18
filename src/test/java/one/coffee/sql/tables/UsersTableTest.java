package one.coffee.sql.tables;

import one.coffee.BaseTest;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsersTableTest extends BaseTest {

    static {
        UsersTableTest.table = UsersTable.INSTANCE;
    }

    @Test
    void ok() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        User user = new User(userId, userCity, userState, userConnection);
        UsersTable.putUser(user);

        User savedUser = UsersTable.getUserById(userId);

        assertEquals(savedUser.getId(), userId);
        assertEquals(user.getCity(), userCity);
        assertEquals(user.getState(), userState);
        assertEquals(user.getUserConnection(), userConnection);

        UsersTable.deleteUserById(userId);

        assertThrows(Exception.class, () -> UsersTable.getUserById(userId));
    }

    @Test
    void sameUserId() {
        final long user1Id = 123;
        final String user1City = "St. Petersburg";
        final UserState user1State = UserState.DEFAULT;
        final UserConnection user1Connection = null;
        User user1 = new User(user1Id, user1City, user1State, user1Connection);
        UsersTable.putUser(user1);

        final long user2Id = 123;
        final String user2City = "St. Petersburg";
        final UserState user2State = UserState.DEFAULT;
        final UserConnection user2Connection = null;
        User user2 = new User(user2Id, user2City, user2State, user2Connection);
        assertThrows(Exception.class, () -> UsersTable.putUser(user2));

        User savedUser1 = UsersTable.getUserById(user1Id);

        assertEquals(savedUser1.getId(), user1Id);
        assertEquals(user1.getCity(), user1City);
        assertEquals(user1.getState(), user1State);
        assertEquals(user1.getUserConnection(), user1Connection);
    }

}
