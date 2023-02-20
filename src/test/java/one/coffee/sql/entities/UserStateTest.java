package one.coffee.sql.entities;

import one.coffee.DBTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserStateTest {

    @Test
    void invalidStateType() {
        assertThrows(Exception.class, () -> new UserState(null));
    }

}
