package one.coffee.sql.tables;

import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStatesTableTest
        extends TableTest {

    @Disabled("TODO Перезапись в базу по id")
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
