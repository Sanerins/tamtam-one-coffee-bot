package one.coffee.sql.entities;

import one.coffee.sql.user.UsersTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    void ok() throws SQLException {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        new User(userId, userCity, state, connectionId);
    }

    @Test
    void invalidUserId() {
        final long userId = -1;
        final String userCity = "St. Petersburg";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, state, connectionId));
    }

    @Test
    void invalidUserCity1() {
        final long userId = 123;
        final String userCity = null;
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, state, connectionId));
    }

    @Test
    void invalidUserCity2() {
        final long userId = 123;
        final String userCity = "";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, state, connectionId));
    }

    @Disabled("TODO Validate user city")
    @Test
    void invalidUserCity3() {
        final long userId = 123;
        final String userCity = "abc";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, state, connectionId));
    }

    @Test
    void invalidUserState() throws SQLException {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.ordinal() - 1;
        final long connectionId = -1;

        User user = new User(userId, userCity, stateId, connectionId);
        user.commit();
        User savedUser = UsersTable.getUserByUserId(userId);
        assertEquals(savedUser.getState(), UserState.DEFAULT);
    }

}
