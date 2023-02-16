package one.coffee.sql.tables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WaitingUsersTable extends Table {

    public static final WaitingUsersTable INSTANCE = new WaitingUsersTable();

    private WaitingUsersTable() {
        shortName = "waitingUsers";
        args = List.of(
                Map.entry("id", "BIGINT PRIMARY KEY"),
                Map.entry("userId", "INT REFERENCES users(userId)"),
                Map.entry("stateId", "INT REFERENCES states(stateId)"),
                Map.entry("connectionId", "INT REFERENCES userConnections(id)"),
                Map.entry("city", "VARCHAR(20)")
        );
        init();
    }
}
