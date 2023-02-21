package one.coffee.sql.tables;

import one.coffee.DBTest;
import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;

import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsersTableTest
        extends TableTest {

    @DBTest(nUsers = 1)
    void ok(User user) {
        User savedUser = UsersTable.getUserById(user.getId());

        assertEquals(savedUser.getId(), user.getId());
        assertEquals(savedUser.getCity(), user.getCity());
        assertEquals(savedUser.getStateId(), user.getStateId());
        assertEquals(savedUser.getConnectionId(), user.getConnectionId());

        UsersTable.deleteUserById(user.getId()); // FIXME

        assertThrows(Exception.class, () -> UsersTable.getUserById(user.getId()));
    }

//    @DBTest
//    void rewriteUser() {
//        final long user1Id = 123;
//        final String user1City = "St. Petersburg";
//        final UserState user1State = UserState.DEFAULT;
//        final UserConnection user1Connection = null;
//        User user1 = new User(user1Id, user1City, user1State, user1Connection);
//        UsersTable.putUser(user1);
//
//        final long user2Id = 123;
//        final String user2City = "Moscow";
//        final UserState user2State = UserState.DEFAULT;
//        final UserConnection user2Connection = null;
//        User user2 = new User(user2Id, user2City, user2State, user2Connection);
//        UsersTable.putUser(user2);
//
//        User savedUser1 = UsersTable.getUserById(user1Id);
//
//        assertEquals(savedUser1.getId(), user1Id);
//        assertEquals(user1.getCity(), user1City);
//        assertEquals(user1.getState(), user1State);
//        assertEquals(user1.getUserConnection(), user1Connection);
//    }
//
//    @DBTest
//    void doublePut() {
//        UsersTable.putUser(new User(123, "MSK", UserState.DEFAULT, null));
//        UsersTable.putUser(new User(123, "SPB", UserState.CHATTING, null));
//
//        AtomicBoolean hasTwoEntities = new AtomicBoolean();
//        DB.executeQuery("SELECT COUNT(*) AS n FROM " + UsersTable.INSTANCE.getShortName(), rs -> {
//            if (!rs.next()) {
//                throw new IllegalStateException("Users weren't saved in DB!");
//            }
//            hasTwoEntities.set(rs.getInt("n") > 1);
//        });
//        assertFalse(hasTwoEntities.get());
//
//        User savedUser = UsersTable.getUserById(123);
//        assertEquals(savedUser.getCity(), "SPB");
//        assertEquals(savedUser.getState(), UserState.CHATTING);
//    }

    @Override
    protected Table getTable() {
        return UsersTable.INSTANCE;
    }

}
