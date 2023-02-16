package one.coffee.sql;

import one.coffee.sql.tables.StatesTable;
import one.coffee.sql.tables.Table;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UserStatesTable;
import one.coffee.sql.tables.UsersTable;
import one.coffee.sql.tables.WaitingUsersTable;
import one.coffee.utils.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String DB_NAME = "OneCoffee.db";
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;
    private static final Connection CONNECTION;
    private static final Statement STATEMENT;

    static {
        try {
            CONNECTION = DriverManager.getConnection(CONNECTION_URL); // auto-commit mode with multithreading support
            STATEMENT = CONNECTION.createStatement();

            // Предполагается, что значения в эту таблицу уже будут подгружены извне единожды
            StatesTable.init();
            UserStatesTable.init();
            UsersTable.init();
            WaitingUsersTable.init();
            UserConnectionsTable.init();
        } catch (SQLException e) { // Считаю, что зафейленная инициализация БД - критическая ситуация для приложения,
                                   // поэтому ложим всё приложение, если что-то пошло тут не так
            LOG.error("DB creation is failed! Details: {}", Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    public static void createTableFor(Table table) {
        try {
            STATEMENT.execute("CREATE TABLE IF NOT EXISTS " + table.toString());
        } catch (SQLException e) {
            LOG.error("Unable to create table {}. Details: ", table, e);
            throw new RuntimeException(e); // Это фатальная ситуация, если мы не смогли создать таблицу
        }
    }

    public static void executeQuery(String query, SQLAction sqlAction) {
        try (ResultSet rs = STATEMENT.executeQuery(query)) {
            sqlAction.run(rs);
        } catch (SQLException e) {
            LOG.warn("Something went wrong when executing query: {}. Details: {}", query, e);
        }
    }

    public interface SQLAction {
        SQLAction NO_ACTION = rs -> {};
        void run(ResultSet rs) throws SQLException;
    }
}
