package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserDao;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@Component
public class HighloadTest
        extends ResourceTest {

    @Autowired
    private UserDao userDao;

    @Disabled("База поддерживает 4K GET-Rps в однопоток")
    @DBTest()
    void getUsers(List<User> users) {
        final int N = users.size();
        for (User user : users) {
            userDao.get(user.getId());
        }
    }

    @Disabled("База поддерживает 20 PUT-Rps в однопоток")
    @DBTest()
    void putUsers() {
        for (int i = 0; i < 20; ++i) {
            User user = new User(i + 1, "City" + (i + 1), UserState.DEFAULT, String.valueOf(-1));
            userDao.save(user);
        }
    }

    @Disabled("База поддерживает 100 DELETE-Rps в однопоток")
    @DBTest()
    void deleteUsers(List<User> users) {
        final int N = users.size();
        for (User user : users) {
            userDao.delete(user);
        }
    }

}
