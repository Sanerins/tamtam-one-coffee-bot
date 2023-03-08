package one.coffee.sql.user_connection;

import one.coffee.sql.DB;
import one.coffee.sql.Service;
import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserDao;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserConnectionService
        implements Service<UserConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserConnectionService INSTANCE;
    private static final UserConnectionDao userConnectionDao = StaticContext.USER_CONNECTION_DAO;

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

    public Optional<UserConnection> getByUserId(long user1Id, long /*unused*/ user2Id) {
        return userConnectionDao.getByUserId(user1Id, user2Id);
    }

    // TODO Мб тут возвращать флажок состояния, получилось сохранить или нет?
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
        long conId = userConnectionDao.getByUserId(user1Id, SQLUtils.NO_ID).get().getId();
        commitUsersConnection(conId, user1Id, user2Id, UserState.CHATTING);
    }

    @Override
    public void delete(UserConnection userConnection) {
        long user1Id = userConnection.getUser1Id();
        long user2Id = userConnection.getUser2Id();
        commitUsersConnection(SQLUtils.NO_ID, user1Id, user2Id, UserState.WAITING);
        userConnectionDao.delete(userConnection);
    }

    private boolean isConnected(long userId) {
        return getByUserId(userId, SQLUtils.NO_ID).isPresent();
    }

    private void commitUsersConnection(long connectionId, long user1Id, long user2Id, UserState state) {
        User user1 = StaticContext.USER_SERVICE.get(user1Id).get();
        user1.setState(state);
        user1.setConnectionId(connectionId);
        StaticContext.USER_SERVICE.save(user1);

        User user2 = StaticContext.USER_SERVICE.get(user2Id).get();
        user2.setState(state);
        user2.setConnectionId(connectionId);
        StaticContext.USER_SERVICE.save(user2);
    }

}
