package one.coffee.sql.user;

import one.coffee.sql.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Component
public class UserService
        implements Service<User> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserDao userDao;

    // TODO Enhance
    private static boolean isValidCity(String city) {
        return city != null && !city.trim().isEmpty();
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
            LOG.warn("Invalid User id! Got {}", user);
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

}
