package one.coffee.sql.user_connection;

import one.coffee.sql.Service;
import one.coffee.sql.states.UserConnectionState;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
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

    public List<UserConnection> getByUserId(long userId) {
        return userConnectionDao.getByUserId(userId);
    }

    public List<UserConnection> getByUserIdAndUserConnectionState(long userId, UserConnectionState state) {
        return getByUserId(userId).stream()
                .filter(uc -> uc.getState().equals(state))
                .toList();
    }

    public Optional<UserConnection> getByUserIdsAndUserConnectionState(UserConnection userConnection) {
        return getByUserIdAndUserConnectionState(userConnection.getUser1Id(), userConnection.getState()).stream()
                .filter(uc -> {
                    if (uc.getUser1Id() == userConnection.getUser1Id()) {
                        return uc.getUser2Id() == userConnection.getUser2Id();
                    } else {
                        return uc.getUser1Id() == userConnection.getUser1Id();
                    }
                })
                .findAny();
    }

    public Optional<UserConnection> getInProgressConnectionByUserId(long userId) {
        List<UserConnection> inProgressUserConnections =
                getByUserIdAndUserConnectionState(userId, UserConnectionState.IN_PROGRESS);
        if (inProgressUserConnections.isEmpty()) {
            return Optional.empty();
        } else if (inProgressUserConnections.size() > 1) {
            LOG.warn("Several userConnections with state 'IN_PROGRESS'! Expected only one.");
            return Optional.empty();
        }
        return Optional.of(inProgressUserConnections.get(0));
    }

    public List<UserConnection> getSuccessfulConnectionsByUserId(long userId) {
        return getByUserIdAndUserConnectionState(userId, UserConnectionState.SUCCESSFUL);
    }

    public List<UserConnection> getUnsuccessfulConnectionsByUserId(long userId) {
        return getByUserIdAndUserConnectionState(userId, UserConnectionState.UNSUCCESSFUL);
    }

    public long getConnectedUserId(long userId) {
        Optional<UserConnection> optionalUserConnection = getInProgressConnectionByUserId(userId);
        if (optionalUserConnection.isEmpty()) {
            return SQLUtils.DEFAULT_ID;
        }
        UserConnection userConnection = optionalUserConnection.get();
        return userConnection.getUser1Id() == userId ? userConnection.getUser2Id() : userConnection.getUser1Id();
    }

    public Optional<User> getConnectedUser(long userId) {
        long connectedUserId = getConnectedUserId(userId);
        return userService.get(connectedUserId);
    }

    @Override
    public void save(UserConnection userConnection) {
        long userConnectionId = userConnection.getId();
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        if (userConnectionId <= 0) {
            //TODO Но не факт, что они сконнекчены друг с другом, можно попытаться выпарсить этот случай.
            if (isConnected(user1Id) || isConnected(user2Id)) {
                LOG.warn("{} or {} has already connected!", user1Id, user2Id);
                return;
            }
        } else if (userConnectionDao.get(userConnectionId).isEmpty()) {
            LOG.warn("No UserConnection with id {}", userConnectionId);
            return;
        }

        userConnectionDao.save(userConnection);
        Optional<UserConnection> userConnectionOptional = getByUserIdsAndUserConnectionState(userConnection);
        if (userConnectionOptional.isEmpty()) {
            LOG.error("Error while trying to completely save user connection: not found {}", userConnection);
        } else if (UserConnectionState.IN_PROGRESS.equals(userConnection.getState())){
            long conId = userConnectionOptional.get().getId();
            commitUsersConnection(conId, user1Id, user2Id, UserState.CHATTING);
        }
    }

    @Override
    public void delete(UserConnection userConnection) {
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        commitUsersConnection(SQLUtils.DEFAULT_ID, user1Id, user2Id, UserState.WAITING);
        userConnection.setState(UserConnectionState.UNSUCCESSFUL);
        save(userConnection);
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
        userService.get(userId).ifPresentOrElse(user -> {
            user.setState(userState);
            user.setConnectionId(connectionId);
            userService.save(user);
        }, () -> {
            // TODO Тут нет никакой возможности восстановить данные юзера, поэтому надо бы придумать,
            //  как сообщить пользователю, что ничего не получилось...
            LOG.warn("Can't commit user connection for user {}: no such user", userId);
        });
    }
}
