package one.coffee.sql;

import one.coffee.bot.ContextConf;
import one.coffee.sql.user.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ContextConfiguration(classes = ContextConf.class)
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
