package one.coffee.sql.tables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WaitingUsersTable extends Table {

    public static final WaitingUsersTable INSTANCE = new WaitingUsersTable();

    private WaitingUsersTable() {
        shortName = "waitingUsers";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("userId", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("stateId", "INT REFERENCES states(stateId) ON DELETE SET NULL"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id) ON DELETE CASCADE"),
                Map.entry("city", "VARCHAR(20)")
        );
        init();
    }
}
