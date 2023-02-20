package one.coffee.sql;

import one.coffee.sql.tables.Table;
import one.coffee.sql.tables.TableTest;
import one.coffee.sql.tables.UserStatesTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DBBasicTest extends TableTest {

    @Test
    void putNull() {
        assertThrows(Exception.class, () -> DB.putEntity(getTable(), null));
    }

    @Test
    void deleteFromNull() {
        assertThrows(Exception.class, () -> DB.deleteEntityById(null, 1));
    }

    @Test
    void deleteNonExistentId1() {
        assertThrows(Exception.class, () -> DB.deleteEntityById(getTable(), -1));
    }

    @Test
    void deleteNonExistentId2() {
        assertThrows(Exception.class, () -> DB.deleteEntityById(getTable(), 123));
    }

    @Override
    protected Table getTable() {
        return UserStatesTable.INSTANCE;
    }

}
