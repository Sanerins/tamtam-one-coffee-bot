package one.coffee.sql.user_connection;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import one.coffee.sql.Service;
import one.coffee.sql.user.User;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.sql.utils.UserConnectionState;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserConnectionService implements Service<UserConnection> {

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

    public List<UserConnection> getByUserId(long userId) {
        return userConnectionDao.getByUserId(userId);
    }

    public List<UserConnection> getByUserIdAndUserConnectionState(long userId, UserConnectionState state) {
        return getByUserId(userId).stream()
                .filter(uc -> uc.getState().equals(state))
                .toList();
    }

    public Optional<UserConnection> getInProgressConnectionByUserId(long userId) {
        List<UserConnection> inProgressUserConnections = getByUserIdAndUserConnectionState(userId, UserConnectionState.IN_PROGRESS);
        if (inProgressUserConnections.isEmpty()) {
            return Optional.empty();
        } else if (inProgressUserConnections.size() > 1) {
            LOG.warn("Several userConnections with state 'IN_PROGRESS'! Expected only one.");
            return Optional.empty();
        }
        return Optional.of(inProgressUserConnections.get(0));
    }

    public List<UserConnection> getUnsuccessfulConnectionsByUserId(long userId) {
        return getByUserIdAndUserConnectionState(userId, UserConnectionState.UNSUCCESSFUL);
    }

    public List<UserConnection> getSuccessfulConnectionsByUserId(long userId) {
        return getByUserIdAndUserConnectionState(userId, UserConnectionState.SUCCESSFUL);
    }

    public long getConnectedUserId(long userId) {
        Optional<UserConnection> userConnectionOptional = getInProgressConnectionByUserId(userId);
        if (userConnectionOptional.isPresent()) {
            UserConnection userConnection = userConnectionOptional.get();
            return userConnection.getUser1Id() == userId ? userConnection.getUser2Id() : userConnection.getUser1Id();
        } else {
            return SQLUtils.DEFAULT_ID;
        }
    }

    public Optional<User> getConnectedUser(long userId) {
        long connectedUserId = getConnectedUserId(userId);
        return StaticContext.USER_SERVICE.get(connectedUserId);
    }

    @Override
    public Optional<UserConnection> save(UserConnection userConnection) {
        long userConnectionId = userConnection.getId();
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        if (userConnectionId <= 0) {
            //TODO Но не факт, что они сконнекчены друг с другом, можно попытаться выпарсить этот случай.
            if (isConnected(user1Id) || isConnected(user2Id)) {
                LOG.warn("{} or {} has already connected!", user1Id, user2Id);
                return Optional.empty();
            }
        } else if (userConnectionDao.get(userConnectionId).isEmpty()) {
            LOG.warn("No UserConnection with id {}", userConnectionId);
            return Optional.empty();
        }

        userConnectionDao.save(userConnection);
        Optional<UserConnection> optionalConnection = getInProgressConnectionByUserId(user1Id);
        if (optionalConnection.isEmpty()) {
            LOG.warn("Can't save user connection: {}", userConnection);
        } else {
            long conId = optionalConnection.get().getId();
            commitUsersConnection(conId, user1Id, user2Id, UserState.CHATTING);
        }
        return optionalConnection;
    }

    @Override
    public void delete(UserConnection userConnection) {
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        commitUsersConnection(SQLUtils.DEFAULT_ID, user1Id, user2Id, UserState.WAITING);
    }

    public boolean haveConnection(long user1Id, long user2Id) {
        return getConnectedUserId(user1Id) == user2Id;
    }

    public boolean haveNotConnection(long user1Id, long user2Id) {
        return !haveConnection(user1Id, user2Id);
    }

    private boolean isConnected(long userId) {
        return getInProgressConnectionByUserId(userId).isPresent();
    }

    private void commitUsersConnection(long connectionId, long user1Id, long user2Id, UserState state) {
        commitUserConnection(connectionId, user1Id, state);
        commitUserConnection(connectionId, user2Id, state);
    }

    private void commitUserConnection(long connectionId, long userId, UserState userState) {
        StaticContext.USER_SERVICE.get(userId).ifPresentOrElse(user -> {
            user.setState(userState);
            user.setConnectionId(connectionId);
            StaticContext.USER_SERVICE.save(user);
        }, () -> {
            // TODO Тут нет никакой возможности восстановить данные юзера, поэтому надо бы придумать,
            //  как сообщить пользователю, что ничего не получилось...
            LOG.warn("Can't commit user connection for user {}: no such user", userId);
        });
    }

}
