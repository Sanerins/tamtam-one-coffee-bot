package one.coffee.utils;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.keyboards.WaitingKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user_connection.UserConnection;

@Component
public class DefaultStateUtils extends StateUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public StateResult handleStart(long userId, String username) {
        Optional<User> chattingCandidateOptional = userService.getChattingCandidate(userId);
        if (chattingCandidateOptional.isEmpty()) {
            startTheWait(userId, username);
            return new StateResult(Result.ResultState.SUCCESS);
        }

        User chattingCandidate = chattingCandidateOptional.get();
        UserConnection userConnection = new UserConnection(userId, chattingCandidate.getId());
        userConnectionService.save(userConnection);

        messageSender.sendMessage(userId,
                """
                        Я нашел вам собеседника!
                        Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                        Список команд, доступных во время беседы можно открыть на /help\s""");
        messageSender.sendMessage(chattingCandidate.getId(),
                """
                        Я нашел вам собеседника!
                        Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                        Список команд, доступных во время беседы можно открыть на /help\s""");
        return new StateResult(StateResult.ResultState.SUCCESS);
    }

    public StateResult handleProfile(long userId, String username) {

        Optional<User> optionalSender = userService.get(userId);
        User sender;
        sender = optionalSender.orElseGet(() -> new User(userId, "Cyberpunk2077", UserState.DEFAULT, username));
        sender.setState(UserState.PROFILE_DEFAULT);
        userService.save(sender);
        messageSender.sendKeyboard(userId, new FillProfileKeyboard());
        return new StateResult(Result.ResultState.SUCCESS);
    }

    private void startTheWait(long userId, String username) {

        Optional<User> optionalSender = userService.get(userId);
        User sender;
        // НЕ РЕФАКТОРИТЬ!!! TODO
        // См. возможные причины в OneCoffeeUpdateHandler::map(MessageCreatedUpdate)
        sender = optionalSender.orElseGet(() -> new User(userId, "Cyberpunk2077", UserState.DEFAULT, username));
        sender.setState(UserState.WAITING);
        userService.save(sender);
        messageSender.sendKeyboard(userId,
                new WaitingKeyboard("Вы успешно добавлены в список ждущий пользователей! Ожидайте начала диалога! "));
    }
}
