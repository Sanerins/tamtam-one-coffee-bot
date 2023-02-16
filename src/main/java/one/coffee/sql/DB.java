package one.coffee.sql;

import one.coffee.utils.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.swing.plaf.nimbus.State;

public class DB implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String DB_NAME = "OneCoffee.db";
    private static final String RELPATH = "src/main/resources/" + DB_NAME;
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_NAME;
    public static final Statement statement;

    static {
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_URL);
            statement = conn.createStatement();

            // Предполагается, что значения в эту таблицу уже будут подгружены извне единожды
            statement.execute("CREATE TABLE IF NOT EXISTS states(" +
                    " id BIGINT PRIMARY KEY," +
                    " stateId INT NOT NULL," +
                    " stateName VARCHAR(10) NOT NULL" + // Для информативности?
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS userStates(" +
                    " id BIGINT PRIMARY KEY," +
                    " stateId INT REFERENCES states(stateId)" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS users(" +
                    " id BIGINT NOT NULL," +
                    " userId BIGINT PRIMARY KEY," +
                    " stateId INT NOT NULL," +
                    " city varchar(20)" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS waitingUsers(" +
                    " id BIGINT PRIMARY KEY," +
                    " userId INT REFERENCES users(userId)" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS userConnections(" +
                    " id BIGINT PRIMARY KEY," +
                    " user1Id INT REFERENCES users(userId)," +
                    " user2Id INT REFERENCES users(userId)" +
                    ")");

            conn.commit();
        } catch (SQLException e) { // Считаю, что зафейленная инициализация БД - критическая ситуация для приложения,
                                   // поэтому ложим всё приложение, если что-то пошло тут не так
            LOG.error("DB creation is failed! Details: {}", Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }

    public ConcurrentMap<Long, UserState> getUserStates() {
        ConcurrentMap<Long, UserState> res = new ConcurrentHashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT userId, users.stateId AS stateId" +
                    " FROM users" +
                    " JOIN userStates" +
                    " ON users.stateId = userStates.stateId"
            );

            while (rs.next()) {
                long userId = rs.getLong("userId");
                int stateId = rs.getInt("stateId");
                res.put(userId, UserState.values()[stateId]); // Дрозд говорил, что `Enum::values` каждый раз выделяет память. Надо этоп проверить
            }
        } catch (SQLException e) {
            LOG.warn("Unable to process GET-query for DB! Details: {}", Arrays.toString(e.getStackTrace()));
        }
        return res;
    }

    public ConcurrentMap<Long, Long> getUserConnections() {
        ConcurrentMap<Long, Long> res = new ConcurrentHashMap<>();
        try {
            ResultSet rs = statement.executeQuery("SELECT user1Id, user2Id" +
                    " FROM userConnections"
            );

            while (rs.next()) {
                long user1Id = rs.getLong("user1Id");
                long user2Id = rs.getLong("user2Id");
                res.put(user1Id, user2Id);
                res.put(user2Id, user1Id);
            }
        } catch (SQLException e) {
            LOG.warn("Unable to process GET-query for DB! Details: {}", Arrays.toString(e.getStackTrace()));
        }
        return res;
    }

    @Override
    public void close() throws IOException {
        try {
            statement.close();
        } catch (SQLException e) {
            LOG.error("Unable to release DB resources! Details: {}", Arrays.toString(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }
}
