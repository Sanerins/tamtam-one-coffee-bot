package one.coffee.sql;

import one.coffee.bot.ContextConf;
import one.coffee.sql.user.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ContextConfiguration(classes = ContextConf.class, loader= AnnotationConfigContextLoader.class)
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
