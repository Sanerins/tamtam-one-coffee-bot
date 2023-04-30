package one.coffee.sql.user_connection;

import one.coffee.sql.Service;
import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;


@Component
public class UserConnectionService
        implements Service<UserConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private UserConnectionDao userConnectionDao;
    @Autowired
    private UserService userService;

    @Override
    public Optional<UserConnection> get(long id) {
        return userConnectionDao.get(id);
    }

    public Optional<UserConnection> getByUserId(long userId) {
        return userConnectionDao.getByUserId(userId);
    }

    public long getConnectedUserId(long userId) {
        Optional<UserConnection> optionalUserConnection = getByUserId(userId);
        if (optionalUserConnection.isEmpty()) {
            return SQLUtils.NO_ID;
        }
        UserConnection userConnection = optionalUserConnection.get();
        return userConnection.getUser1Id() == userId ? userConnection.getUser2Id() : userConnection.getUser1Id();
    }

    @Override
    public void save(UserConnection userConnection) {
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        if (userConnection.getId() <= 0) {
            if (isConnected(user1Id) || isConnected(user2Id)) { // Но не факт, что они сконнекчены друг с другом.
                // Можно попытаться выпарсить этот случай.
                LOG.warn("{} or {} has already connected!", user1Id, user2Id);
                return;
            }
        } else {
            if (userConnectionDao.get(userConnection.getId()).isEmpty()) {
                LOG.warn("No UserConnection with id {}", userConnection.getId());
                return;
            }
        }

        userConnectionDao.save(userConnection);
        Optional<UserConnection> optionalConnection = getByUserId(user1Id);
        if (optionalConnection.isEmpty()) {
            LOG.warn("Can't save user connection: {}", userConnection);
        } else {
            long conId = optionalConnection.get().getId();
            commitUsersConnection(conId, user1Id, user2Id, UserState.CHATTING);
        }
    }

    @Override
    public void delete(UserConnection userConnection) {
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        commitUsersConnection(SQLUtils.NO_ID, user1Id, user2Id, UserState.WAITING);
        userConnectionDao.delete(userConnection);
    }

    private boolean isConnected(long userId) {
        return getByUserId(userId).isPresent();
    }

    private void commitUsersConnection(long connectionId, long user1Id, long user2Id, UserState state) {
        Optional<User> optionalUser1 = userService.get(user1Id);
        if (optionalUser1.isEmpty()) {
            LOG.warn("User with id {} is absent in DB!", user1Id);
            return; // TODO Тут нет никакой возможности восстановить данные юзера, поэтому надо бы придумать, как сообщить пользователю, что ничего не получилось...
        }
        User user1 = optionalUser1.get();
        user1.setState(state);
        user1.setConnectionId(connectionId);
        userService.save(user1);

        Optional<User> optionalUser2 = userService.get(user2Id);
        if (optionalUser2.isEmpty()) {
            LOG.warn("User with id {} is absent in DB!", user2Id);
            return; // TODO Тут нет никакой возможности восстановить данные юзера, поэтому надо бы придумать, как сообщить пользователю, что ничего не получилось...
        }
        User user2 = optionalUser2.get();
        user2.setState(state);
        user2.setConnectionId(connectionId);
        userService.save(user2);
    }

}
