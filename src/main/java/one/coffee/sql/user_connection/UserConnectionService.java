package one.coffee.sql.user_connection;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import one.coffee.sql.Service;
import one.coffee.sql.user.User;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserConnectionService
        implements Service<UserConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserConnectionDao userConnectionDao = StaticContext.USER_CONNECTION_DAO;
    private static UserConnectionService INSTANCE;

    private UserConnectionService() {
    }

    public static UserConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserConnectionService();
        }
        return INSTANCE;
    }

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
            return SQLUtils.DEFAULT_ID;
        }
        UserConnection userConnection = optionalUserConnection.get();
        return userConnection.getUser1Id() == userId ? userConnection.getUser2Id() : userConnection.getUser1Id();
    }

    public Optional<User> getConnectedUser(long userId) {
        long connectedUserId = getConnectedUserId(userId);
        return StaticContext.USER_SERVICE.get(connectedUserId);
    }

    @Override
    public Optional<UserConnection> save(UserConnection userConnection) {
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        if (userConnection.getId() <= 0) {
            if (isConnected(user1Id) || isConnected(user2Id)) { // Но не факт, что они сконнекчены друг с другом.
                // Можно попытаться выпарсить этот случай.
                LOG.warn("{} or {} has already connected!", user1Id, user2Id);
                return Optional.empty();
            }
        } else {
            if (userConnectionDao.get(userConnection.getId()).isEmpty()) {
                LOG.warn("No UserConnection with id {}", userConnection.getId());
                return Optional.empty();
            }
        }

        userConnectionDao.save(userConnection);
        Optional<UserConnection> optionalConnection = getByUserId(user1Id);
        if (optionalConnection.isEmpty()) {
            LOG.warn("Can't save user connection: {}", userConnection);
            return Optional.empty();
        } else {
            long conId = optionalConnection.get().getId();
            commitUsersConnection(conId, user1Id, user2Id, UserState.CHATTING);
            return optionalConnection;
        }
    }

    @Override
    public void delete(UserConnection userConnection) {
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        commitUsersConnection(SQLUtils.DEFAULT_ID, user1Id, user2Id, UserState.WAITING);
        userConnectionDao.delete(userConnection);
    }

    private boolean isConnected(long userId) {
        return getByUserId(userId).isPresent();
    }

    private void commitUsersConnection(long connectionId, long user1Id, long user2Id, UserState state) {
        Optional<User> optionalUser1 = StaticContext.USER_SERVICE.get(user1Id);
        if (optionalUser1.isEmpty()) {
            LOG.warn("User with id {} is absent in DB!", user1Id);
            return; // TODO Тут нет никакой возможности восстановить данные юзера, поэтому надо бы придумать, как сообщить пользователю, что ничего не получилось...
        }
        User user1 = optionalUser1.get();
        user1.setState(state);
        user1.setConnectionId(connectionId);
        StaticContext.USER_SERVICE.save(user1);

        Optional<User> optionalUser2 = StaticContext.USER_SERVICE.get(user2Id);
        if (optionalUser2.isEmpty()) {
            LOG.warn("User with id {} is absent in DB!", user2Id);
            return; // TODO Тут нет никакой возможности восстановить данные юзера, поэтому надо бы придумать, как сообщить пользователю, что ничего не получилось...
        }
        User user2 = optionalUser2.get();
        user2.setState(state);
        user2.setConnectionId(connectionId);
        StaticContext.USER_SERVICE.save(user2);
    }

}
