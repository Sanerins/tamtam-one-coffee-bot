package one.coffee.sql.tables;

import java.util.List;

public class UserConnectionsTable extends Table {

    private UserConnectionsTable() {
    }

    public static void init() {
        Table.init("userConnections", List.of(
                "id BIGINT PRIMARY KEY",
                "user1Id INT REFERENCES users(userId)",
                "user2Id INT REFERENCES users(userId)"
        ));
    }
}
