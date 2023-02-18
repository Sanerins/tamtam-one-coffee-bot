package one.coffee;

import one.coffee.sql.DB;
import one.coffee.sql.tables.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

    protected static Table table;

    @AfterEach
    void cleanupTable() {
        DB.cleanupTable(table);
    }
}
