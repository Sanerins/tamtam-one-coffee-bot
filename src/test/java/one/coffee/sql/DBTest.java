package one.coffee.sql;

import one.coffee.BaseTest;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UserStatesTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DBTest extends BaseTest {

    static {
        DBTest.table = UserStatesTable.INSTANCE;
    }

    @Test
    void putNull() {
        assertThrows(Exception.class, () -> DB.putEntity(DBTest.table, null));
    }

    @Test
    void deleteFromNull() {
        assertThrows(Exception.class, () -> DB.deleteEntityById(null, 1));
    }

    @Test
    void deleteNonExistentId1() {
        assertThrows(Exception.class, () -> DB.deleteEntityById(DBTest.table, -1));
    }

    @Test
    void deleteNonExistentId2() {
        assertThrows(Exception.class, () -> DB.deleteEntityById(DBTest.table, 123));
    }

}
