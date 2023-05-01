package one.coffee.sql;

import java.util.List;

import one.coffee.DBTest;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.StaticContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest
        extends ResourceTest {

    private static final UserService userService = StaticContext.USER_SERVICE;

    @Test
    void ok1() {
        long userId = 123;
        String userCity = UserService.userCities.stream().findAny().get();
        User user = User.build()
                .setId(userId)
                .setCity(userCity)
                .get();

        userService.save(user);

        User savedUser = userService.get(userId).get();

        assertEquals(savedUser.getId(), userId);
        assertEquals(savedUser.getState(), UserState.DEFAULT);
        assertEquals(savedUser.getCity(), userCity);
        assertEquals(savedUser.getConnectionId(), SQLUtils.DEFAULT_ID);
    }

    @Test
    void invalidUserId() {
        User user = User.build().get();
        userService.save(user);
        assertTrue(userService.get(SQLUtils.DEFAULT_ID).isEmpty());
    }

    @Test
    void invalidUserCity1() {
        final long userId = 123;
        final String userCity = null;
        User user = User.build()
                .setId(userId)
                .setCity(userCity)
                .get();

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @Test
    void invalidUserCity2() {
        final long userId = 123;
        final String userCity = "";
        User user = User.build()
                .setId(userId)
                .setCity(userCity)
                .get();

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @Test
    void invalidUserCity3() {
        final long userId = 123;
        final String userCity = "abc";
        User user = User.build()
                        .setId(userId)
                        .setCity(userCity)
                        .get();
        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
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

        String newUserCity = UserService.userCities.stream()
                .filter(city -> !city.equals(user.getCity()))
                .findAny()
                .get();
        user.setCity(newUserCity);
        userService.save(user);
        User savedUser = userService.get(user.getId()).get();

        assertEquals(savedUser.getId(), user.getId());
        assertEquals(savedUser.getCity(), newUserCity);
        assertEquals(savedUser.getState(), user.getState());
        assertEquals(savedUser.getConnectionId(), user.getConnectionId());
    }

}
