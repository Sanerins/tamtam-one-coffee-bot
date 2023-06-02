package one.coffee.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionService;

@Component
public class StateUtils {

    @Autowired
    protected MessageSender messageSender;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserConnectionService userConnectionService;

}
