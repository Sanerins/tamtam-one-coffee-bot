package one.coffee.sql;

import one.coffee.sql.entities.Entity;
import one.coffee.sql.tables.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

// В рамках SQLite БД CREATE-запрос эквивалентен PUT-запросу.
// TODO Сейчас спарсенные значения из базы почти никак не проверяются. Нужно это исправить.

// TODO Решить проблему SQL-инъекций.
public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String DB_NAME = "OneCoffee.db"; // Будет находиться прямо в корне проекта
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;
    private static final Connection CONNECTION;
    private static final Statement STATEMENT;

    static {
        try {
            // Auto-commit mode with multithreading support
            // TODO Проверить, что многопоток действительно поддерживается (дока, профилирование, тестирование).
            // Если на самом деле он не поддерживается, то создать пул коннекшенов, как было на NoSQL.
            CONNECTION = DriverManager.getConnection(CONNECTION_URL); // auto-commit mode with multithreading support
            STATEMENT = CONNECTION.createStatement();

        } catch (SQLException e) { // Считаю, что зафейленная инициализация БД - критическая ситуация для приложения,
            // поэтому ложим всё приложение, если что-то пошло тут не так
            throw new RuntimeException("DB creation is failed! Details: " + e.getMessage());
        }
    }

    private DB() {
    }

    public static void createTable(Table table) {
        Objects.requireNonNull(table, "Table can't be null!");

        executeQuery("CREATE TABLE IF NOT EXISTS " + table);
        LOG.info("Created table: {}", table.getShortName());
    }

    public static void dropTable(Table table) {
        Objects.requireNonNull(table, "Table can't be null!");

        executeQuery("DROP TABLE IF EXISTS " + table.getShortName());
        LOG.info("Dropped table: {}", table.getShortName());
    }

    public static void cleanupTable(Table table) {
        Objects.requireNonNull(table, "Table can't be null!");

        executeQuery("DELETE FROM " + table.getShortName());
        executeQuery("UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = '" + table.getShortName() + "'");
        LOG.info("Cleanup table: {}", table.getShortName());
    }

    // TODO Оптимизировать подстановку строк предкомпиляцией общих паттернов (например, для MessageFormat)
    public static void putEntity(Table table, Entity entity) {
        Objects.requireNonNull(table, "Table can't be null!");
        Objects.requireNonNull(entity, "Entity can't be null!");

        executeQuery("INSERT OR REPLACE INTO " + table.getSignature(entity)
                + " VALUES " + entity.sqlArgValues());
        //LOG.info("Put entity: {}", entity);
    }

    public static void deleteEntity(Table table, Entity entity) throws SQLException {
        Objects.requireNonNull(table, "Table can't be null!");

        if (entity.getId() <= 0) {
            throw new IllegalArgumentException("'id' must be a positive number! Got: " + entity.getId());
        }

        if (!hasEntity(table, entity)) {
            LOG.warn("Table {} has not entity with `id` = {}", table.getSignature(entity), entity.getId());
        }

        executeQuery("DELETE FROM " + table.getShortName() + " WHERE id = " + entity.getId());
        //LOG.info("Delete entity from table '{}' with 'id' = {}", table.getShortName(), entity.getId());
    }

    public static boolean hasEntity(Table table, Entity entity) throws SQLException {
        AtomicBoolean isPresent = new AtomicBoolean();
        executeQuery("SELECT *" +
                        " FROM " + table.getShortName() +
                        " WHERE id = " + entity.getId(),
                rs -> isPresent.set(rs.next()));
        return isPresent.get();
    }

    public static synchronized void executeQuery(String query) {
        try {
            STATEMENT.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Error when executing query: " + query + ".\n" +
                    "Details: " + e.getMessage());
        }
    }

    public static synchronized void executeQuery(String query, SQLAction sqlAction) throws SQLException {
        try (ResultSet rs = STATEMENT.executeQuery(query)) {
            sqlAction.run(rs);
        }
    }

    public interface SQLAction {
        void run(ResultSet rs) throws SQLException;
    }
}
