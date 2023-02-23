package one.coffee.sql.tables;

import one.coffee.DBTest;
import one.coffee.sql.entities.User;

import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsersTableTest
        extends TableTest {

    @DBTest(nUsers = 1)
    void ok(List<User> users) throws SQLException {
        User user = users.get(0);
        UsersTable.deleteUser(user);
        assertDoesNotThrow(() -> UsersTable.getUserByUserId(user.getId()));
    }

    @DBTest(nUsers = 1)
    void rewriteUser(List<User> users) throws SQLException {
        User user = users.get(0);

        String newUserCity = user.getCity() + "777";
        user.setCity(newUserCity);
        user.commit();
        User savedUser = UsersTable.getUserByUserId(user.getId());

        assertEquals(savedUser.getId(), user.getId());
        assertEquals(savedUser.getCity(), newUserCity);
        assertEquals(savedUser.getState(), user.getState());
        assertEquals(savedUser.getConnectionId(), user.getConnectionId());
    }

}
