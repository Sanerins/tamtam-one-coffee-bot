package one.coffee.sql.user;

import one.coffee.sql.DB;
import one.coffee.sql.Service;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService
        implements Service<User> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static UserService INSTANCE;
    private static final UserDao userDao = StaticContext.USER_DAO;

    private UserService() {
    }

    public static UserService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
    }

    @Override
    public Optional<User> get(long id) {
        return userDao.get(id);
    }

    public List<User> getWaitingUsers(long n) {
        return userDao.getWaitingUsers(n);
    }

    @Override
    public void save(User user) {
        if (user.getId() <= 0) {
            LOG.warn("Invalid User id! Got {}", user)
            return;
        }

        if (!isValidCity(user.getCity())) {
            LOG.warn("Invalid user city!! Got {}", user.getCity());
            return;
        }

        userDao.save(user);
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }

    private static boolean isValidCity(String city) {
        return city != null && !city.trim().isEmpty();
    }

}
