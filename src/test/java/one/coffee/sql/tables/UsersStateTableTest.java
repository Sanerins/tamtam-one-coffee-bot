package one.coffee.sql.tables;

import one.coffee.BaseTest;
import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsersStateTableTest extends BaseTest {

    static {
        UsersStateTableTest.table = UserStatesTable.INSTANCE;
    }

    @Test
    void ok() {
        UserState userState = UserState.DEFAULT;
        UserStatesTable.putUserState(userState);

        UserState savedUserState = UserStatesTable.getUserStateById(userState.getId());
        assertEquals(savedUserState, userState);

        UserStatesTable.deleteUserStateById(userState.getId());
        assertThrows(Exception.class, () -> UserStatesTable.getUserStateById(userState.getId()));
    }

    @Test
    void sameUserStateId() {
        UserState userState = UserState.DEFAULT;
        UserStatesTable.putUserState(userState);
        assertThrows(Exception.class, () -> UserStatesTable.putUserState(userState));
    }

}
