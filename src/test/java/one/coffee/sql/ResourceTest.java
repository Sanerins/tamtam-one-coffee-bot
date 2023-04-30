package one.coffee.sql;

import one.coffee.sql.user.UserDao;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionDao;
import one.coffee.sql.user_connection.UserConnectionService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class ResourceTest {

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected UserConnectionDao userConnectionDao;

    @Autowired
    protected UserConnectionService userConnectionService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DB DB;

    @AfterEach
    void cleanup() {
        List<Dao<?>> daos = List.of(userDao, userConnectionDao);
        for (Dao<?> dao : daos) {
            DB.cleanupTable(dao);
        }
    }

}
