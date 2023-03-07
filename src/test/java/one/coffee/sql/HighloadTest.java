package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.TableTest;
import one.coffee.sql.user.UsersTable;
import org.junit.jupiter.api.Disabled;

import java.sql.SQLException;
import java.util.List;

public class HighloadTest
        extends TableTest {

    @Disabled("База поддерживает 4K GET-Rps в однопоток")
    @DBTest(nUsers = 4000)
    void getUsers(List<User> users) throws SQLException {
        final int N = users.size();
        for (User user : users) {
            UsersTable.getUserByUserId(user.getId());
        }
    }

    @Disabled("База поддерживает 20 PUT-Rps в однопоток")
    @DBTest(nUsers = 0)
    void putUsers() throws SQLException {
        final int N = 20;
        for (int i = 0; i < N; ++i) {
            User user = new User(i + 1, "City" + (i + 1), UserState.DEFAULT, -1);
            UsersTable.putUser(user);
        }
    }

    @Disabled("База поддерживает 100 DELETE-Rps в однопоток")
    @DBTest(nUsers = 100)
    void deleteUsers(List<User> users) throws SQLException {
        final int N = users.size();
        for (User user : users) {
            UsersTable.deleteUser(user);
        }
    }

}
