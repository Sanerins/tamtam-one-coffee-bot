package one.coffee.sql.tables;

import one.coffee.DBTest;
import one.coffee.sql.entities.User;
import org.junit.jupiter.api.Disabled;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsersTableTest
        extends TableTest {

    @DBTest(nUsers = 1)
    void ok(User user) {
        User savedUser = UsersTable.getUserByUserId(user.getUserId());

        assertEquals(savedUser.getUserId(), user.getUserId());
        assertEquals(savedUser.getCity(), user.getCity());
        assertEquals(savedUser.getStateId(), user.getStateId());
        assertEquals(savedUser.getConnectionId(), user.getConnectionId());

        UsersTable.deleteUserById(user.getId());

        assertThrows(Exception.class, () -> UsersTable.getUserByUserId(user.getUserId()));
    }

    @Disabled("TODO Перезапись в базу по id")
    @DBTest(nUsers = 1)
    void rewriteUser(User user) {
        String newUserCity = user.getCity() + "777";
        user.setCity(newUserCity);
        user.commit();
        User savedUser = UsersTable.getUserByUserId(user.getUserId());

        assertEquals(savedUser.getId(), user.getId());
        assertEquals(savedUser.getUserId(), user.getUserId());
        assertEquals(savedUser.getCity(), newUserCity);
        assertEquals(savedUser.getStateId(), user.getStateId());
        assertEquals(savedUser.getConnectionId(), user.getConnectionId());
    }

    @Override
    protected Table getTable() {
        return UsersTable.INSTANCE;
    }

}
