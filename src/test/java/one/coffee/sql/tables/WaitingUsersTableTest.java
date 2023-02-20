package one.coffee.sql.tables;

import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaitingUsersTableTest extends TableTest {

    @Test
    void ok() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState userState = UserState.WAITING;
        final UserConnection userConnection = null;
        User user = new User(userId, userCity, userState, userConnection);
        UsersTable.putUser(user);

        WaitingUsersTable.putWaitingUser(user);

        List<User> waitingUsers = WaitingUsersTable.getWaitingUsers();
        assertFalse(waitingUsers.isEmpty());

        WaitingUsersTable.deleteWaitingUserById(userId);
        waitingUsers = WaitingUsersTable.getWaitingUsers();
        assertTrue(waitingUsers.isEmpty());
    }

    @Test
    void invalidUserState() {
        final long userId = 123;
        final String userCity = "St. Petersburg";
        final UserState userState = UserState.DEFAULT;
        final UserConnection userConnection = null;
        User user = new User(userId, userCity, userState, userConnection);
        UsersTable.putUser(user);

        assertThrows(Exception.class, () -> WaitingUsersTable.putWaitingUser(user));
    }

    @Override
    protected Table getTable() {
        return WaitingUsersTable.INSTANCE;
    }

}
