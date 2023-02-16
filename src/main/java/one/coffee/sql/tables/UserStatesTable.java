package one.coffee.sql.tables;

import java.util.List;

public class UserStatesTable extends Table {

    private UserStatesTable() {
    }

    public static void init() {
        Table.init("userStates", List.of(
                "id BIGINT PRIMARY KEY",
                "stateId INT REFERENCES states(stateId)"
        ));
    }
}
