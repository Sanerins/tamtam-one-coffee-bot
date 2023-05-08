package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import org.junit.jupiter.api.Disabled;

import java.util.List;

public class HighloadTest
        extends ResourceTest {

    @Disabled("База поддерживает 4K GET-Rps в однопоток")
    @DBTest(nUsers = 4000)
    void getUsers(List<User> users) {
        for (User user : users) {
            userDao.get(user.getId());
        }
    }

    @Disabled("База поддерживает 20 PUT-Rps в однопоток")
    @DBTest(nUsers = 0)
    void putUsers() {
        for (int i = 0; i < 20; ++i) {
            User user = new User(i + 1, "City" + (i + 1), UserState.DEFAULT, String.valueOf(-1));
            userDao.save(user);
        }
    }

    @Disabled("База поддерживает 100 DELETE-Rps в однопоток")
    @DBTest(nUsers = 100)
    void deleteUsers(List<User> users) {
        for (User user : users) {
            userDao.delete(user);
        }
    }

}
