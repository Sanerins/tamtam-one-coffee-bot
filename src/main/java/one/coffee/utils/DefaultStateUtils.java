package one.coffee.utils;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.keyboards.InConversationKeyboard;
import one.coffee.keyboards.InitialProfileStateKeyboard;
import one.coffee.keyboards.StartConversationKeyboard;
import one.coffee.keyboards.WaitingKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnection;

@Component
public class DefaultStateUtils extends StateUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public StateResult handleStart(long userId, String username) {
        Optional<User> user = userService.get(userId);

        if (user.isEmpty() || !UserService.checkProfileValid(user.get())) {
            User sender = user.orElseGet(() -> new User(userId, null, UserState.DEFAULT, username));
            sender.setState(UserState.PROFILE_DEFAULT);
            userService.save(sender);
            messageSender.sendKeyboard(userId, new InitialProfileStateKeyboard("Ваш профиль еще не полностью заполнен!"));
            return new StateResult(Result.ResultState.SUCCESS);
        }

        Optional<User> chattingCandidateOptional = userService.getChattingCandidate(userId);
        if (chattingCandidateOptional.isEmpty()) {
            startTheWait(userId, username);
            return new StateResult(Result.ResultState.SUCCESS);
        }

        User chattingCandidate = chattingCandidateOptional.get();
        UserConnection userConnection = new UserConnection(userId, chattingCandidate.getId());
        userConnectionService.save(userConnection);

        messageSender.sendKeyboard(userId, new StartConversationKeyboard("""
                        Я нашел вам собеседника!
                        Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                        Список команд с кнопками всегда можно вызвать на /help\s"""));
        messageSender.sendKeyboard(chattingCandidate.getId(), new StartConversationKeyboard("""
                        Я нашел вам собеседника!
                        Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                        Список команд с кнопками всегда можно вызвать на /help\s"""));
        return new StateResult(StateResult.ResultState.SUCCESS);
    }

    public StateResult handleProfile(long userId, String username) {

        Optional<User> optionalSender = userService.get(userId);
        User sender;
        sender = optionalSender.orElseGet(() -> new User(userId, null, UserState.DEFAULT, username));
        sender.setState(UserState.PROFILE_DEFAULT);
        userService.save(sender);
        messageSender.sendKeyboard(userId, new FillProfileKeyboard("Какое из действий вы хотите сделать со своим профилем?"));
        return new StateResult(Result.ResultState.SUCCESS);
    }

    private void startTheWait(long userId, String username) {

        Optional<User> optionalSender = userService.get(userId);
        User sender;
        // НЕ РЕФАКТОРИТЬ!!! TODO
        // См. возможные причины в OneCoffeeUpdateHandler::map(MessageCreatedUpdate)
        sender = optionalSender.orElseGet(() -> new User(userId, null, UserState.DEFAULT, username));
        sender.setState(UserState.WAITING);
        userService.save(sender);
        messageSender.sendKeyboard(userId,
                new WaitingKeyboard("Вы успешно добавлены в список ждущий пользователей! Ожидайте начала диалога! "));
    }
}
