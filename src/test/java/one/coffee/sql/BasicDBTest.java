package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.utils.StaticContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicDBTest
        extends ResourceTest {

    @DBTest(nUsers = 0)
    void putNull() {
        assertThrows(Exception.class, () -> DB.putEntity(getDao(), null));
    }

    @DBTest(nUsers = 0)
    void deleteFromNull() {
        assertThrows(Exception.class, () -> DB.deleteEntity(null, null));
    }

    @DBTest(nUsers = 0)
    protected Dao<?> getDao() {
        return StaticContext.USER_DAO;
    }

}
