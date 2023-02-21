package one.coffee.sql.tables;

import one.coffee.sql.DB;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public abstract class TableTest {

    @AfterEach
    void cleanupTableAfterEach() {
        List<Table> tables = List.of(UsersTable.INSTANCE, UserConnectionsTable.INSTANCE);
        for (Table table : tables) {
            DB.cleanupTable(table);
        }
    }

}
