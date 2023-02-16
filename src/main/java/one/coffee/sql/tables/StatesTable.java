package one.coffee.sql.tables;

import java.util.List;

public class StatesTable extends Table {

    private StatesTable() {
    }

    public static void init() {
        Table.init("states", List.of(
                "id BIGINT PRIMARY KEY",
                "stateId INT NOT NULL",
                "stateName VARCHAR(10) NOT NULL"
        ));
    }
}
