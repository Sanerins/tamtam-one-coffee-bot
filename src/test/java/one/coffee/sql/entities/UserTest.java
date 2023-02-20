package one.coffee.sql.entities;

import one.coffee.DBTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
        new User(userId, userCity, userState, userConnection);
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

}
