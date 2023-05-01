package one.coffee.sql;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Подумать, нужно ли объединять DB с Dao
public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String DB_NAME = "OneCoffee.db";
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;
    public static Connection CONNECTION;
    private static Statement STATEMENT;

    static {
        try {
            CONNECTION = DriverManager.getConnection(CONNECTION_URL);
            STATEMENT = CONNECTION.createStatement();
        } catch (SQLException e) {
            LOG.error("DB creation is failed!", e);
            StaticContext.getBot().stop();
        }
    }

    private DB() {
    }

    public static void createTable(Dao<?> dao) {
        try {
            Objects.requireNonNull(dao, "Table can't be null!");
            String sql = "CREATE TABLE IF NOT EXISTS " + dao;
            PreparedStatement stmt = CONNECTION.prepareStatement(sql);
            executeQuery(stmt);

            //executeQuery("CREATE TABLE IF NOT EXISTS " + dao);
            LOG.info("Created table: `{}`", dao.getShortName());
        } catch (SQLException e) {
            LOG.warn("When creating table `{}`", dao.getShortName(), e);
        }
    }

    public static void dropTable(Dao<?> dao) {
        Objects.requireNonNull(dao, "Table can't be null!");

        try {
            String sql = "DROP TABLE IF EXISTS " + dao.getShortName();
            PreparedStatement stmt = CONNECTION.prepareStatement(sql);
            executeQuery(stmt);

            //executeQuery("DROP TABLE IF EXISTS " + dao.getShortName());
            LOG.info("Dropped table: `{}`", dao.getShortName());
        } catch (SQLException e) {
            LOG.warn("When dropping table `{}`", dao.getShortName(), e);
        }
    }

    public static void cleanupTable(Dao<?> dao) {
        Objects.requireNonNull(dao, "Table can't be null!");

        try {
            String deleteSql = "DELETE FROM ?";
            PreparedStatement deleteStmt = CONNECTION.prepareStatement(deleteSql);
            deleteStmt.setString(1, dao.getShortName());
            executeQuery(deleteStmt);

            String updateSql = "SELECT setval('users_id_seq', 0)";
            PreparedStatement updateStmt = CONNECTION.prepareStatement(updateSql);
            executeQuery(updateStmt);

            //executeQuery("DELETE FROM " + dao.getShortName());
            //executeQuery("UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = '" + dao.getShortName() + "'");
            LOG.info("Cleanup table: {}", dao.getShortName());
        } catch (SQLException e) {
            LOG.warn("When dropping table {}", dao, e);
        }
    }

    public static void putEntity(Dao<?> dao, Entity entity) {
        Objects.requireNonNull(dao, "Table can't be null!");
        Objects.requireNonNull(entity, "Entity can't be null!");

        try {
            String sql = "INSERT OR REPLACE INTO " + dao.getSignature(entity) +  " VALUES " + entity.sqlArgValues();
            PreparedStatement stmt = CONNECTION.prepareStatement(sql);
            executeQuery(stmt);

            //executeQuery("INSERT OR REPLACE INTO " + dao.getSignature(entity) + " VALUES " + entity.sqlArgValues());
            LOG.info("Put entity: {}", entity);
        } catch (SQLException e) {
            LOG.warn("When putting entity {}", entity, e);
        }
    }

    public static void deleteEntity(Dao<?> dao, Entity entity) {
        Objects.requireNonNull(dao, "Table can't be null!");
        if (!hasEntity(dao, entity)) {
            LOG.warn("Table {} has not entity with `id` = {}", dao.getSignature(entity), entity.getId());
        }

        try {
            String sql = "DELETE FROM " + dao.getShortName() + " WHERE id = ?";
            PreparedStatement stmt = CONNECTION.prepareStatement(sql);
            stmt.setLong(1, entity.getId());
            executeQuery(stmt);

            //executeQuery("DELETE FROM " + dao.getShortName() + " WHERE id = " + entity.getId());
            LOG.info("Delete entity from table '{}' with 'id' = {}", dao.getShortName(), entity.getId());
        } catch (SQLException e) {
            LOG.warn("When deleting entity {}", entity, e);
        }
    }

    public static boolean hasEntity(Dao<?> dao, Entity entity) {
        AtomicBoolean isPresent = new AtomicBoolean();
        try {
            String sql = "SELECT * FROM " + dao.getShortName() + " WHERE id = ?";
            PreparedStatement stmt = CONNECTION.prepareStatement(sql);
            stmt.setLong(1, entity.getId());

            executeQueryWithActionForResult(stmt, rs -> isPresent.set(rs.next()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isPresent.get();
    }

    public static synchronized void executeQuery(String query) {
        try {
            STATEMENT.execute(query);
        } catch (SQLException e) {
            LOG.warn("Error while executing query to DB!", e);
        }
    }

    public static synchronized void executeQuery(PreparedStatement stmt) {
        try {
            stmt.execute();
        } catch (SQLException e) {
            LOG.warn("Error while executing query to DB!", e);
        }
    }

    public static synchronized void executeQueryWithActionForResult(PreparedStatement stmt, SQLAction sqlAction) {
        try (ResultSet rs = stmt.executeQuery()) {
            sqlAction.run(rs);
        } catch (SQLException e) {
            LOG.warn("Error while executing query:\n'\n{}'\nto DB!", stmt, e);
        }
    }

    public interface SQLAction {
        void run(ResultSet rs) throws SQLException;
    }

}
