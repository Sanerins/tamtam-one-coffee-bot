package one.coffee.sql;

import one.coffee.sql.tables.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String DB_NAME = "sql/OneCoffee.db";
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;
    private static final Connection CONNECTION;
    private static final Statement STATEMENT;

    static {
        try {
            CONNECTION = DriverManager.getConnection(CONNECTION_URL); // auto-commit mode with multithreading support
            STATEMENT = CONNECTION.createStatement();
        } catch (SQLException e) { // Считаю, что зафейленная инициализация БД - критическая ситуация для приложения,
                                   // поэтому ложим всё приложение, если что-то пошло тут не так
            LOG.error("DB creation is failed! Details: {}", e);
            throw new RuntimeException(e);
        }
    }

    public static void createTableFor(Table table) {
        executeQuery("CREATE TABLE IF NOT EXISTS " + table.toString());
        LOG.info("Created table: {}", table.getShortName());
    }

    public static void dropTableFor(String name) {
        executeQuery("DROP TABLE IF EXISTS " + name);
        LOG.info("Dropped table: {}", name);
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
