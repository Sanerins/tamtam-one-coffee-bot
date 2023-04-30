package one.coffee.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO Подумать, нужно ли объединять DB с Dao
public class DB {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private Connection CONNECTION;
    private Statement STATEMENT;

    public DB(Properties properties) {
        try {
            // Auto-commit mode with multithreading support
            // TODO Проверить, что многопоток действительно поддерживается (дока, профилирование, тестирование).
            // Если на самом деле он не поддерживается, то создать пул коннекшенов, как было на NoSQL.
            CONNECTION = DriverManager.getConnection(properties.getProperty("db.URL"));
            STATEMENT = CONNECTION.createStatement();
        } catch (SQLException e) {
            // Считаю, что зафейленная инициализация БД - критическая ситуация для приложения,
            // поэтому ложим всё приложение, если что-то пошло тут не так
            LOG.error("DB creation is failed!", e);
            // FIXME как-то аккуратно выключиться, жду предложений
            System.exit(1);
        }
    }

    @SuppressWarnings("unused")
    public void createTable(Dao<?> dao) {
        Objects.requireNonNull(dao, "Table can't be null!");

        executeQuery("CREATE TABLE IF NOT EXISTS " + dao);
        LOG.info("Created table: {}", dao.getShortName());
    }

    @SuppressWarnings("unused")
    public void dropTable(Dao<?> dao) {
        Objects.requireNonNull(dao, "Table can't be null!");

        executeQuery("DROP TABLE IF EXISTS " + dao.getShortName());
        LOG.info("Dropped table: {}", dao.getShortName());
    }

    public void cleanupTable(Dao<?> dao) {
        Objects.requireNonNull(dao, "Table can't be null!");

        executeQuery("DELETE FROM " + dao.getShortName());
        executeQuery("UPDATE `sqlite_sequence` SET `seq` = 0 WHERE `name` = '" + dao.getShortName() + "'");
        LOG.info("Cleanup table: {}", dao.getShortName());
    }

    public void putEntity(Dao<?> dao, Entity entity) {
        Objects.requireNonNull(dao, "Table can't be null!");
        Objects.requireNonNull(entity, "Entity can't be null!");

        executeQuery("INSERT OR REPLACE INTO " + dao.getSignature(entity)
                + " VALUES " + entity.sqlArgValues());
        //LOG.info("Put entity: {}", entity);
    }

    public void deleteEntity(Dao<?> dao, Entity entity) {
        Objects.requireNonNull(dao, "Table can't be null!");
        if (!hasEntity(dao, entity)) {
            LOG.warn("Table {} has not entity with `id` = {}", dao.getSignature(entity), entity.getId());
        }
        executeQuery("DELETE FROM " + dao.getShortName() + " WHERE id = " + entity.getId());
        //LOG.info("Delete entity from table '{}' with 'id' = {}", table.getShortName(), entity.getId());
    }

    public boolean hasEntity(Dao<?> dao, Entity entity) {
        AtomicBoolean isPresent = new AtomicBoolean();
        executeQuery("SELECT *" +
                        " FROM " + dao.getShortName() +
                        " WHERE id = " + entity.getId(),
                rs -> isPresent.set(rs.next()));
        return isPresent.get();
    }

    public synchronized void executeQuery(String query) {
        try {
            STATEMENT.execute(query);
        } catch (SQLException e) {
            LOG.warn("Error while executing query to DB!", e);
        }
    }

    public synchronized void executeQuery(String query, SQLAction sqlAction) {
        try (ResultSet rs = STATEMENT.executeQuery(query)) {
            sqlAction.run(rs);
        } catch (SQLException e) {
            LOG.warn("Error while executing query to DB!", e);
        }
    }

    public interface SQLAction {
        void run(ResultSet rs) throws SQLException;
    }

}
