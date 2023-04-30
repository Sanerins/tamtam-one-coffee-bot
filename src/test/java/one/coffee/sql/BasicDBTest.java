package one.coffee.sql;

import one.coffee.sql.user.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Component
public class BasicDBTest {
    @Autowired
    protected UserDao userDao;

    @Autowired
    protected DB DB;

    @Test
    void putNull() {
        assertThrows(Exception.class, () -> DB.putEntity(userDao, null));
    }

    @Test
    void deleteFromNull() {
        assertThrows(Exception.class, () -> DB.deleteEntity(null, null));
    }

}
