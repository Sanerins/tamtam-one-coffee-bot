package one.coffee.sql.tables;

import java.util.List;

public class WaitingUsersTable extends Table {

    private WaitingUsersTable() {
    }

    public static void init() {
        Table.init("waitingUsers", List.of(
                "id BIGINT PRIMARY KEY",
                "userId INT REFERENCES users(userId)"
        ));
    }
}
