package one.coffee.sql.entities;

import one.coffee.sql.tables.UsersTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

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
    }

    @Test
    void invalidUserId() {
        final long userId = -1;
        final String userCity = "St. Petersburg";
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        assertThrows(Exception.class, () -> new User(userId, userCity, userState, userConnection));
    }

    @Test
    void invalidUserCity1() {
        final long userId = 123;
        final String userCity = null;
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        assertThrows(Exception.class, () -> new User(userId, userCity, userState, userConnection));
    }

    @Test
    void invalidUserCity2() {
        final long userId = 123;
        final String userCity = "";
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        assertThrows(Exception.class, () -> new User(userId, userCity, userState, userConnection));
    }

    @Disabled("TODO Validate user city")
    @Test
    void invalidUserCity3() {
        final long userId = 123;
        final String userCity = "abc";
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        assertThrows(Exception.class, () -> new User(userId, userCity, userState, userConnection));
    }

    @Test
    void invalidUserState() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState userState = null;
        final UserConnection userConnection = null;
        assertThrows(Exception.class, () -> new User(userId, userCity, userState, userConnection));
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
