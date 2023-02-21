package one.coffee.sql.tables;

import one.coffee.sql.entities.UserState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStatesTableTest
        extends TableTest {

    @Test
    void sameUserStateId() {
        assertThrows(Exception.class, () -> UserStatesTable.putUserState(UserState.DEFAULT));
    }

}
