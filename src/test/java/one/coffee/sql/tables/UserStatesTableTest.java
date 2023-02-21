package one.coffee.sql.tables;

import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStatesTableTest
        extends TableTest {

    @Test
    void sameUserStateId() {
        UserState userState = UserState.DEFAULT;
        UserStatesTable.putUserState(userState);
        assertThrows(Exception.class, () -> UserStatesTable.putUserState(userState));
    }

    @Override
    protected Table getTable() {
        return UserStatesTable.INSTANCE;
    }

}
