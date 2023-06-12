package one.coffee.utils;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;

@Component
public class WaitingStateUtils extends StateUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public StateResult handleStop(long userId, String username) {
        Optional<User> optionalSender = userService.get(userId);
        User sender;
        // См. возможные причины в OneCoffeeUpdateHandler::visit(MessageCreatedUpdate)
        sender = optionalSender.orElseGet(() -> new User(userId, null, UserState.DEFAULT, username));
        sender.setState(UserState.DEFAULT);
        userService.save(sender);
        messageSender.sendKeyboard(userId, new DefaultStateKeyboard("Ты успешно вышел из очереди!"));
        return new StateResult(Result.ResultState.SUCCESS);
    }
}
