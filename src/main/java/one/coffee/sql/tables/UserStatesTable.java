package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.UserState;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// Предполагается, что значения в эту таблицу уже будут подгружены извне единожды
public class UserStatesTable extends Table {

    public static final UserStatesTable INSTANCE = new UserStatesTable();

    private UserStatesTable() {
        shortName = "userStates";
        args = List.of(
                Map.entry("id", "BIGINT PRIMARY KEY"),
                Map.entry("stateId", "INT NOT NULL")
        );
        init();
    }

    public static UserState getUserStateById(long id) {
        AtomicReference<UserState> userState = new AtomicReference<>();
        String query = MessageFormat.format(
                "SELECT * FROM {0}",
                INSTANCE.shortName
        );

        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No userState with such id in DB: " + id);
            }

            UserState.StateType stateType = UserState.StateType.fromId(rs.getLong("stateId"));
            userState.set(new UserState(stateType));
        });

        return userState.get();
    }

    public static void putUserState(UserState userState) {
        DB.putEntity(INSTANCE, userState);
    }

    public static void deleteUserStateById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }
}
