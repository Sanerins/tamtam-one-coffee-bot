package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.utils.StaticContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicDBTest {

    @Test
    void putNull() {
        assertThrows(Exception.class, () -> DB.putEntity(StaticContext.USER_DAO, null));
    }

    @Test
    void deleteFromNull() {
        assertThrows(Exception.class, () -> DB.deleteEntity(null, null));
    }

}
