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

            LOG.info("Dropped table: `{}`", dao.getShortName());
        } catch (SQLException e) {
            LOG.warn("When dropping table `{}`", dao.getShortName(), e);
        }
    }

    public static void cleanupTable(Dao<?> dao) {
        Objects.requireNonNull(dao, "Table can't be null!");

        try {
            String deleteSql = "DELETE FROM " + dao.getShortName();
            PreparedStatement deleteStmt = CONNECTION.prepareStatement(deleteSql);
            executeQuery(deleteStmt);

            String updateSql = "UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = " + StaticContext.quote(dao.getShortName());
            PreparedStatement updateStmt = CONNECTION.prepareStatement(updateSql);
            executeQuery(updateStmt);

            LOG.info("Cleanup table: {}", dao.getShortName());
        } catch (SQLException e) {
            LOG.warn("When dropping table {}", dao, e);
        }
    }

    public static <T extends Entity> void putEntity(Dao<?> dao, T entity) {
        Objects.requireNonNull(dao, "Table can't be null!");
        Objects.requireNonNull(entity, "Entity can't be null!");

        try {
            String sql = "INSERT OR REPLACE INTO " + dao.getSignature(entity) +  " VALUES " + entity.sqlArgValues();
            PreparedStatement stmt = CONNECTION.prepareStatement(sql);
            executeQuery(stmt);

            LOG.info("Put entity: {}", entity);
        } catch (SQLException e) {
            LOG.warn("When putting entity {}", entity, e);
        }
    }

    public static <T extends Entity> void deleteEntity(Dao<?> dao, T entity) {
        Objects.requireNonNull(dao, "Table can't be null!");
        if (!hasEntity(dao, entity)) {
            LOG.warn("Table {} has not entity with `id` = {}", dao.getSignature(entity), entity.getId());
            return;
        }

        try {
            String sql = "DELETE FROM " + dao.getShortName() + " WHERE id = ?";
            PreparedStatement stmt = CONNECTION.prepareStatement(sql);
            stmt.setLong(1, entity.getId());
            executeQuery(stmt);

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

            executeQuery(stmt, rs -> isPresent.set(rs.next()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isPresent.get();
    }

    public static void executeQuery(PreparedStatement stmt) {
        executeQuery(stmt, SQLCallback.EMPTY);
    }

    public static synchronized void executeQuery(PreparedStatement stmt, SQLCallback sqlCallback) {
        try {
            if (stmt.execute()) {
                try (ResultSet rs = stmt.executeQuery()) {
                    sqlCallback.run(rs);
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while executing query to DB:\n'\n{}'", stmt, e);
        }
    }

    public interface SQLCallback {
        SQLCallback EMPTY = rs -> {};
        void run(ResultSet rs) throws SQLException;
    }

}
