package one.coffee.sql.entities;

import one.coffee.BaseTest;
import one.coffee.sql.tables.UserStatesTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStateTest extends BaseTest {

    static {
        UserStateTest.table = UserStatesTable.INSTANCE;
    }

    @Test
    void invalidStateType() {
        assertThrows(Exception.class, () -> new UserState(null));
    }
}
