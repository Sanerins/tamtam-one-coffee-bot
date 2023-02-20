package one.coffee.sql.tables;

import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class UserConnectionsTable extends Table {

    public static final UserConnectionsTable INSTANCE = new UserConnectionsTable();

    private UserConnectionsTable() {
        shortName = "userConnections";
        args = List.of(
                Map.entry("id", "INTEGER PRIMARY KEY AUTOINCREMENT"),
                Map.entry("user1Id", "INT REFERENCES users(userId) ON DELETE CASCADE"),
                Map.entry("user2Id", "INT REFERENCES users(userId) ON DELETE CASCADE")
        );
        init();
    }

    public static List<UserConnection> getUserConnectionsByUserId(long userId) {
        List<UserConnection> userConnections = new ArrayList<>();
        String query = MessageFormat.format(
                "SELECT DISTINCT {0}.id AS userId, city, stateId, connectionId" +
                        " FROM (" +
                        "    SELECT *" +
                        "    FROM {1}" +                  // TODO Насколько я понимаю, конкатенировать строки в джаве не айс. Насколько это критично для нас?
                        "    WHERE user1Id = " + userId + // MessageFormatter плохо воспринимает большие числа, для него 2077.toString = "2 077", а не "2077"
                        "    OR user2Id = " + userId +    // поэтому костыляем как можем. Есть неповторимый оригинал String.format,
                        ") AS s" +                        // но мне кажется, тяжело каждый раз вводить, какой тип ожидается.
                        "LEFT JOIN {0} ON {0}.userId = " + userId,
                UsersTable.INSTANCE.getShortName(),
                UserConnectionsTable.INSTANCE.getShortName()
        );

        DB.executeQuery(query, rs -> {
            // Тут база выдаст 2*N результатов, сначала идёт инфа о первом юзере в связи, потом о втором.
            // Не нашёл более оптимального способа вытащить эту инфу из базы, если мёржить это в одну строчку,
            // получится мегалютый запрос, который и отладить-то непросто.
            while (rs.next()) {
                long user1Id = rs.getLong("userId");
                String userCity1 = rs.getString("city");
                UserState user1State = new UserState(UserState.StateType.fromId(rs.getLong("stateId")));
                long userConnection1Id = rs.getLong("connectionId");
                User user1 = new User(user1Id, userCity1, user1State, null);
                if (!user1State.equals(UserState.CHATTING)) {
                    throw new IllegalStateException("User: " + user1 + " has illegal 'state' = " + user1State + " while expected 'state' = " + UserState.CHATTING);
                }

                long user2Id = rs.getLong("userId");
                String userCity2 = rs.getString("city");
                UserState user2State = new UserState(UserState.StateType.fromId(rs.getLong("stateId")));
                long userConnection2Id = rs.getLong("connectionId");
                User user2 = new User(user2Id, userCity2, user2State, null);
                if (!user2State.equals(UserState.CHATTING)) {
                    throw new IllegalStateException("User: " + user2 + " has illegal 'state' = " + user2State + " while expected 'state' = " + UserState.CHATTING);
                }

                if (userConnection1Id != userConnection2Id) {
                    throw new IllegalStateException("User: " + user1 + ", 'connectionId' = " + userConnection1Id
                            + ", has another connectionID than User: " + user2 + ", 'connectionId' = " + userConnection2Id);
                }

                UserConnection userConnection = new UserConnection(user1, user2);
                userConnections.add(userConnection);
            }

            // В самом начале это проверить не можем, так как ResultSet::next двигает указатель внутри себя на данные
            if (userConnections.isEmpty()) {
                throw new IllegalArgumentException("No UserConnection for 'userId'=" + userId);
            }
        });

        return userConnections;
    }

    public static void putUserConnection(UserConnection userConnection) {
        DB.putEntity(INSTANCE, userConnection);
    }

    public static void deleteUserConnectionById(long id) {
        DB.deleteEntityById(INSTANCE, id);
    }

}
