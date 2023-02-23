package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.utils.StaticContext;
import org.junit.jupiter.api.AfterEach;

import java.util.List;

public abstract class TableTest {

    @AfterEach
    void cleanupTableAfterEach() {
        List<Table> tables = List.of(StaticContext.USERS_TABLE, StaticContext.USER_CONNECTIONS_TABLE);
        for (Table table : tables) {
            DB.cleanupTable(table);
        }
    }

}
