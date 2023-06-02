package one.coffee.sql;

import one.coffee.DBTest;
import one.coffee.bot.ContextConf;
import one.coffee.sql.states.UserConnectionState;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.utils.SQLUtils;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@SpringBootTest
@ContextConfiguration(classes = ContextConf.class, loader= AnnotationConfigContextLoader.class)
public class UserConnectionServiceTest
        extends ResourceTest {

    @DBTest()
    void ok(List<User> users) {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnectionService.save(userConnection);

        UserConnection savedUserConnection = userConnectionService.getByUserId(user1Id).get(0);
        User savedUser1 = userService.get(user1Id).get();
        User savedUser2 = userService.get(user2Id).get();

        assertEquals(savedUser1.getState(), UserState.CHATTING);
        assertEquals(savedUserConnection.getId(), savedUser1.getConnectionId());

        assertEquals(savedUser2.getState(), UserState.CHATTING);
        assertEquals(savedUserConnection.getId(), savedUser2.getConnectionId());
    }

    @DBTest()
    void twoParallelConnections(List<User> users) {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();
        long user3Id = users.get(2).getId();

        UserConnection users12Connection = new UserConnection(user1Id, user2Id);
        userConnectionService.save(users12Connection);

        UserConnection users23Connection = new UserConnection(user2Id, user3Id);
        userConnectionService.save(users23Connection);

        User savedUser1 = userService.get(user1Id).get();
        User savedUser2 = userService.get(user2Id).get();
        User savedUser3 = userService.get(user3Id).get();

        UserConnection savedUsers12Connection = userConnectionService.getByUserId(user1Id).get(0);

        assertTrue(userConnectionService.getByUserId(user3Id).isEmpty());

        assertEquals(savedUser1.getState(), UserState.CHATTING);
        assertEquals(savedUser1.getConnectionId(), savedUsers12Connection.getId());

        assertEquals(savedUser2.getState(), UserState.CHATTING);
        assertEquals(savedUser2.getConnectionId(), savedUsers12Connection.getId());

        assertNotEquals(savedUser3.getState(), UserState.CHATTING);
        assertEquals(savedUser3.getConnectionId(), SQLUtils.DEFAULT_ID);
    }

    @DBTest()
    void breakConnection(List<User> users) {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnectionService.save(userConnection);
        userConnectionService.delete(userConnection);

        User savedUser1 = userService.get(user1Id).get();
        User savedUser2 = userService.get(user2Id).get();

        assertEquals(savedUser1.getState(), UserState.DEFAULT);
        assertEquals(savedUser1.getConnectionId(), -1);

        assertEquals(savedUser2.getState(), UserState.DEFAULT);
        assertEquals(savedUser2.getConnectionId(), -1);
    }

    @DBTest()
    void validCandidate(List<User> users) {
        long user1Id = users.get(0).getId();
        long user2Id = users.get(1).getId();

        User user3 = users.get(2);
        user3.setState(UserState.WAITING);
        userService.save(user3);
        long user3Id = user3.getId();

        UserConnection userConnection = new UserConnection(user1Id, user2Id);
        userConnectionService.save(userConnection);
        UserConnection savedUserConnection =
                userConnectionService.getByUserIdsAndUserConnectionState(userConnection).get();
        savedUserConnection.setState(UserConnectionState.UNSUCCESSFUL);
        userConnectionService.delete(savedUserConnection);
        UserConnection deletedUserConnection =
                userConnectionService.get(savedUserConnection.getId()).get();
        assertEquals(deletedUserConnection.getState(), UserConnectionState.UNSUCCESSFUL);

        Optional<User> chattingCandidateOptional = userService.getChattingCandidate(user1Id);
        assertTrue(chattingCandidateOptional.isPresent());
        User chattingCandidate = chattingCandidateOptional.get();
        assertEquals(chattingCandidate.getId(), user3Id);
    }

}
