package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.sql.tables.TableTest;
import one.coffee.utils.StaticContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicDBTest
        extends TableTest {

    @DBTest(nUsers = 0)
    void putNull() {
        assertThrows(Exception.class, () -> DB.putEntity(getTable(), null));
    }

    @DBTest(nUsers = 0)
    void deleteFromNull() {
        assertThrows(Exception.class, () -> DB.deleteEntity(null, null));
    }

    @DBTest(nUsers = 0)
    protected Table getTable() {
        return StaticContext.USERS_TABLE;
    }

}
