package one.coffee.sql.user;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import one.coffee.sql.Service;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService
        implements Service<User> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserDao userDao = StaticContext.USER_DAO;
    private static final UserConnectionService userConnectionService = StaticContext.USER_CONNECTION_SERVICE;
    private static UserService INSTANCE;

    private UserService() {
    }

    public static UserService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
    }

    // TODO Enhance
    private static boolean isValidCity(String city) {
        return city != null && !city.trim().isEmpty();
    }

    @Override
    public Optional<User> get(long id) {
        return userDao.get(id);
    }

    public List<User> getWaitingUsers() {
        return getWaitingUsers(Integer.MAX_VALUE);
    }

    public List<User> getWaitingUsers(long n) {
        return userDao.getWaitingUsers(n);
    }

    //TODO подоплёка для нейронки
    public Optional<User> getChattingCandidate(long userId) {
        return getAllChattingCandidates(userId).stream().findAny();
    }

    @Override
    public Optional<User> save(User user) {
        if (user.getId() <= 0) {
            LOG.warn("Invalid User id! Got {}", user);
            return Optional.empty();
        }

        if (!isValidCity(user.getCity())) {
            LOG.warn("Invalid user city! Got {}", user.getCity());
            return Optional.empty();
        }

        userDao.save(user);
        return Optional.of(user);
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }

    private List<User> getAllChattingCandidates(long userId) {
        List<UserConnection> unsuccessfulUserConnections = userConnectionService.getUnsuccessfulConnectionsByUserId(userId);
        return getWaitingUsers().stream()
                .filter(user -> isCandidate(user, unsuccessfulUserConnections))
                .toList();
    }

    private boolean isCandidate(User user, List<UserConnection> unsuccessfulUserConnections) {
        for (UserConnection unsuccessfulUserConnection : unsuccessfulUserConnections) {
            if (unsuccessfulUserConnection.getUser1Id() == user.getId()
                    || unsuccessfulUserConnection.getUser2Id() == user.getId()) {
                return false;
            }
        }
        return true;
    }

}
