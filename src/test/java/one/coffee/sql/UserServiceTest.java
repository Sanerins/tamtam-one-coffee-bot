package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.StaticContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest
        extends ResourceTest {

    private static final UserService userService = StaticContext.USER_SERVICE;

    @Test
    void ok1() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId);

        userService.save(user);

        User savedUser = userService.get(userId).get();

        assertEquals(savedUser.getId(), userId);
        assertEquals(savedUser.getState(), state);
        assertEquals(savedUser.getCity(), userCity);
        assertEquals(savedUser.getConnectionId(), connectionId);
    }

    @Test
    void invalidUserId() {
        final long userId = -1;
        final String userCity = "St. Petersburg";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId);

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @Test
    void invalidUserCity1() {
        final long userId = 123;
        final String userCity = null;
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId);

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @Test
    void invalidUserCity2() {
        final long userId = 123;
        final String userCity = "";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId);

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @Disabled("TODO Validate user city")
    @Test
    void invalidUserCity3() {
        final long userId = 123;
        final String userCity = "abc";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId);

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @Test
    void invalidUserState() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final long stateId = UserState.DEFAULT.ordinal() - 1;
        final long connectionId = -1;
        User user = new User(userId, userCity, stateId, connectionId);

        userService.save(user); // Согласно Саше, в таком случае мы устанавливаем стейт в DEFAULT

        User savedUser = userService.get(userId).get();
        assertEquals(savedUser.getId(), userId);
        assertEquals(savedUser.getState(), UserState.DEFAULT);
        assertEquals(savedUser.getCity(), userCity);
        assertEquals(savedUser.getConnectionId(), connectionId);
    }

    @DBTest(nUsers = 1)
    void ok2(List<User> users) {
        User user = users.get(0);
        userService.delete(user);
        assertTrue(userService.get(user.getId()).isEmpty());
    }

    @DBTest(nUsers = 1)
    void rewriteUser(List<User> users) {
        User user = users.get(0);

        String newUserCity = user.getCity() + "777";
        user.setCity(newUserCity);
        userService.save(user);
        User savedUser = userService.get(user.getId()).get();

        assertEquals(savedUser.getId(), user.getId());
        assertEquals(savedUser.getCity(), newUserCity);
        assertEquals(savedUser.getState(), user.getState());
        assertEquals(savedUser.getConnectionId(), user.getConnectionId());
    }

}
