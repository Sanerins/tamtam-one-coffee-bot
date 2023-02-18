package one.coffee.sql;

import one.coffee.sql.entities.Entity;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.Table;
import one.coffee.sql.tables.UserStatesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// В рамках SQLite БД CREATE-запрос эквивалентен PUT-запросу.
// TODO Сейчас спарсенные значения из базы почти никак не проверяются. Нужно это исправить.
public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String DB_NAME = "OneCoffee.db";
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;
    private static final Connection CONNECTION;
    private static final Statement STATEMENT;

    static {
        try {
            // Auto-commit mode with multithreading support
            // TODO Проверить, что многопоток действительно поддерживается (дока, профилирование). Если на самом деле
            // TODO он не поддерживается, то создать пул коннекшенов, как было на NoSQL.
            CONNECTION = DriverManager.getConnection(CONNECTION_URL); // auto-commit mode with multithreading support
            STATEMENT = CONNECTION.createStatement();

            UserStatesTable.putUserState(UserState.DEFAULT);
            UserStatesTable.putUserState(UserState.WAITING);
            UserStatesTable.putUserState(UserState.CHATTING);
        } catch (SQLException e) { // Считаю, что зафейленная инициализация БД - критическая ситуация для приложения,
                                   // поэтому ложим всё приложение, если что-то пошло тут не так
            LOG.error("DB creation is failed! Details: {}", e);
            throw new RuntimeException(e);
        }
    }

    public static void createTable(Table table) {
        executeQuery("CREATE TABLE IF NOT EXISTS " + table.toString());
        LOG.info("Created table: {}", table.getShortName());
    }

    public static void dropTable(Table table) {
        executeQuery("DROP TABLE IF EXISTS " + table.getShortName());
        LOG.info("Dropped table: {}", table.getShortName());
    }

    // TODO Оптимизировать подстановку строк предкомпиляцией общих паттернов (например, для MessageFormat)
    public static void putEntity(Table table, Entity entity) {
        executeQuery("INSERT OR REPLACE INTO " + table.signature() + " VALUES " + entity.sqlValues());
        LOG.info("Put entity: {}", entity);
    }

    public static void deleteEntityById(Table table, long id) {
        executeQuery("DELETE * FROM " + table.getShortName() + " WHERE id = " + id);
        LOG.info("Delete entity from table {} with id {}", table.signature(), id);
    }

    public static void executeQuery(String query) {
        try {
            STATEMENT.execute(query);
        } catch (SQLException e) {
            LOG.warn("Something went wrong when executing query: {}. Details: {}", query, e);
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
        void run(ResultSet rs) throws SQLException;
    }
}
