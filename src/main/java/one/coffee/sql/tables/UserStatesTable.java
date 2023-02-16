package one.coffee.sql.tables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserStatesTable extends Table {

    public static final UserStatesTable INSTANCE = new UserStatesTable();

    private UserStatesTable() {
        shortName = "serStates";
        args = List.of(
                Map.entry("id", "BIGINT PRIMARY KEY"),
                Map.entry("stateId", "INT REFERENCES states(stateId)")
        );
        init();
    }
}
