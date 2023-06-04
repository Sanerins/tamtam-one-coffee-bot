package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserDao;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionDao;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.sql.utils.SQLUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
public abstract class ResourceTest {

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected UserConnectionDao userConnectionDao;

    @Autowired
    protected UserConnectionService userConnectionService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DB DB;

    @AfterEach
    void cleanup() {
        List<Dao<?>> daos = List.of(userDao, userConnectionDao);
        for (Dao<?> dao : daos) {
            DB.cleanupTable(dao);
        }
    }

    public Stream<? extends Arguments> provideArguments() {
        int nUsers = 5;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < nUsers; ++i) {
            long id = i + 1;
            User user = new User(
                    id,
                    "City" + id,
                    UserState.DEFAULT,
                    SQLUtils.DEFAULT_ID,
                    "Вася Пупкин",
                    "Живу на болоте"
            );
            userService.save(user);
            users.add(user);
        }
        return Stream.of(Arguments.of(users));
    }

}
