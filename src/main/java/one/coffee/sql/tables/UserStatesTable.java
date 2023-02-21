package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.UserState;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// Предполагается, что значения в эту таблицу уже будут подгружены извне единожды
public class UserStatesTable
        extends Table {

    public static final UserStatesTable INSTANCE = new UserStatesTable();

    private UserStatesTable() {
        shortName = "userStates";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("stateId", "INT NOT NULL")
        );
        init();
    }

    public static UserState getUserStateByStateType(UserState.StateType stateType) throws SQLException {
        AtomicReference<UserState> userState = new AtomicReference<>();
        String query = MessageFormat.format("SELECT *" +
                        " FROM {0}" +
                        " WHERE stateId = " + stateType.ordinal(),
                INSTANCE.getShortName());
        DB.executeQuery(query, rs -> {
            if (!rs.next()) {
                throw new SQLException("No state with 'stateType' = " + stateType);
            }
            long id = rs.getLong("id");
            userState.set(new UserState(id, stateType));
        });
        return userState.get();
    }

    public static void putUserState(UserState userState) throws SQLException {
        if (DB.hasEntity(INSTANCE, userState)) {
            throw new IllegalArgumentException("UserState = " + userState + " has already saved in DB!");
        }
        DB.putEntity(INSTANCE, userState);
    }

    public static void deleteUserState(UserState userState) throws SQLException {
        DB.deleteEntity(INSTANCE, userState);
    }
}
