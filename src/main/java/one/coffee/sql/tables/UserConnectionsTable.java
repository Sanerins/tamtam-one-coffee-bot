package one.coffee.sql.tables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserConnectionsTable extends Table {

    public static final UserConnectionsTable INSTANCE = new UserConnectionsTable();

    private UserConnectionsTable() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "BIGINT PRIMARY KEY"),
                Map.entry("user1Id", "INT REFERENCES users(userId)"),
                Map.entry("user2Id", "INT REFERENCES users(userId)")
        );
        init();
    }
}
