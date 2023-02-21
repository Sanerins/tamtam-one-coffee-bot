package one.coffee.sql.entities;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

    @Test
    void ok() throws SQLException {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        new User(userId, userCity, stateId, connectionId);
    }

    @Test
    void invalidUserId() {
        final long userId = -1;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, stateId, connectionId));
    }

    @Test
    void invalidUserCity1() {
        final long userId = 123;
        final String userCity = null;
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, stateId, connectionId));
    }

    @Test
    void invalidUserCity2() {
        final long userId = 123;
        final String userCity = "";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, stateId, connectionId));
    }

    @Disabled("TODO Validate user city")
    @Test
    void invalidUserCity3() {
        final long userId = 123;
        final String userCity = "abc";
        final long stateId = UserState.DEFAULT.getStateId();
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, stateId, connectionId));
    }

    @Test
    void invalidUserState() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.getStateId() - 1;
        final long connectionId = -1;
        assertThrows(Exception.class, () -> new User(userId, userCity, stateId, connectionId));
    }

}
