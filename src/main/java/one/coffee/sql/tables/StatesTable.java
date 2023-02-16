package one.coffee.sql.tables;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Предполагается, что значения в эту таблицу уже будут подгружены извне единожды
public class StatesTable extends Table {

    public static final StatesTable INSTANCE = new StatesTable();

    private StatesTable() {
        shortName = "userStates";
        args = List.of(
                Map.entry("id", "BIGINT PRIMARY KEY"),
                Map.entry("stateId", "INT NOT NULL"),
                Map.entry("stateName", "VARCHAR(10) NOT NULL")
        );
        init();
    }
}
