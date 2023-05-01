package one.coffee.sql.user;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import one.coffee.sql.Service;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService
        implements Service<User> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserDao userDao = StaticContext.USER_DAO;
    public static final Set<String> userCities = Set.of("Москва", "Санкт-Петербург");
    private static UserService INSTANCE;

    private UserService() {
    }

    public static UserService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserService();
        }
        return INSTANCE;
    }

    private static boolean isValidCity(String city) {
        return city != null && userCities.contains(city);
    }

    @Override
    public Optional<User> get(long id) {
        return userDao.get(id);
    }

    public List<User> getWaitingUsers(long n) {
        return userDao.getWaitingUsers(n);
    }

    @Override
    public Optional<User> save(User user) {
        if (user.getId() <= 0) {
            LOG.warn("Invalid User id! Got {}", user);
            return Optional.empty();
        }

        if (!isValidCity(user.getCity())) {
            LOG.warn("Invalid user city! Got '{}'", user.getCity());
            return Optional.empty();
        }

        userDao.save(user);
        return Optional.of(user);
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }

}
