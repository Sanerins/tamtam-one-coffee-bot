package one.coffee;

import one.coffee.sql.DB;
import one.coffee.sql.tables.Table;
import one.coffee.sql.tables.UserStatesTable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTest {

    protected static Table table;

    @AfterEach
    void cleanupTable() {
        // Don't clean up UserStatesTable because all states are fixed
        if (!table.equals(UserStatesTable.INSTANCE)) {
            DB.cleanupTable(table);
        }
    }
}
