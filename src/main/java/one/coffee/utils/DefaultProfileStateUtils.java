package one.coffee.utils;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateResult;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;

@Component
public class DefaultProfileStateUtils extends StateUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public StateResult finishProfile(long userId) {
        User user = userService.get(userId).get();
        if (!user.getUsername().isBlank()
                && !user.getCity().isBlank()
                && !user.getUserInfo().isBlank()) {
            user.setState(UserState.DEFAULT);
            userService.save(user);
            messageSender.sendMessage(user.getId(), "Вы успешно создали профиль. Используйте /help, чтобы посмотреть новые возможности");
        } else {
            messageSender.sendMessage(user.getId(), "Вы еще не полностью заполнили профиль");
        }
        return new StateResult(Result.ResultState.SUCCESS);
    }
}
