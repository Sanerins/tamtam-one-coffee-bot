package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.bot.ContextConf;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@SpringBootTest
@ContextConfiguration(classes = ContextConf.class, loader= AnnotationConfigContextLoader.class)
public class UserServiceTest
        extends ResourceTest {

    @Test
    void ok1() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId, "Вася Пупкин", "Живу на болоте");

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
        User user = new User(userId, userCity, state, connectionId, "Вася Пупкин", "Живу на болоте");

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @Test
    void validUserCity1() {
        final long userId = 123;
        final String userCity = null;
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId, "Вася Пупкин", "Живу на болоте");

        userService.save(user);
        assertTrue(userService.get(userId).isPresent());
    }

    @Test
    void invalidUserCity2() {
        final long userId = 123;
        final String userCity = "";
        final UserState state = UserState.DEFAULT;
        final long connectionId = -1;
        User user = new User(userId, userCity, state, connectionId, "Вася Пупкин", "Живу на болоте");

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
        User user = new User(userId, userCity, state, connectionId, "Вася Пупкин", "Живу на болоте");

        userService.save(user);
        assertTrue(userService.get(userId).isEmpty());
    }

    @DBTest()
    void ok2(List<User> users) {
        User user = users.get(0);
        userService.delete(user);
        assertTrue(userService.get(user.getId()).isEmpty());
    }

    @DBTest()
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
