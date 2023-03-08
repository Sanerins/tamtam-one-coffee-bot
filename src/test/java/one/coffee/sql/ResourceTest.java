package one.coffee.sql;

import one.coffee.sql.DB;
import one.coffee.sql.Dao;
import one.coffee.utils.StaticContext;
import org.junit.jupiter.api.AfterEach;

import java.util.List;

public abstract class ResourceTest {

    @AfterEach
    void cleanup() {
        List<Dao<?>> daos = List.of(StaticContext.USER_DAO, StaticContext.USER_CONNECTION_DAO);
        for (Dao<?> dao : daos) {
            DB.cleanupTable(dao);
        }
    }

}
