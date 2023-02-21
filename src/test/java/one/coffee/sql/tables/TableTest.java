package one.coffee.sql.tables;

import one.coffee.sql.DB;
import org.junit.jupiter.api.AfterEach;

public abstract class TableTest {

    @AfterEach
    void cleanupTable() {
        DB.cleanupTable(getTable());
    }

    protected abstract Table getTable();

}
