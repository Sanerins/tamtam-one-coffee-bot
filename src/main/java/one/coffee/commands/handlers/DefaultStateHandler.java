package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.TestYesNoKeyBoard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user_connection.UserConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Component
public class DefaultStateHandler extends StateHandler {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public UserState getHandlingState() {
        return UserState.DEFAULT;
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(),
                """
                        Список команд бота, доступных для использования:
                        /help - список всех команд
                        /start - начать диалог с пользователем
                        """);
        return new StateResult(StateResult.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/start")
    private StateResult handleStart(Message message) {
        long senderId = message.getSender().getUserId();
        Optional<User> chattingCandidateOptional = userService.getChattingCandidate(senderId);
        if (chattingCandidateOptional.isEmpty()) {
            startTheWait(message);
            return new StateResult(Result.ResultState.SUCCESS);
        }

        User chattingCandidate = chattingCandidateOptional.get();
        UserConnection userConnection = new UserConnection(senderId, chattingCandidate.getId());
        userConnectionService.save(userConnection);

        messageSender.sendMessage(senderId,
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

    private void startTheWait(Message message) {
        long senderId = message.getSender().getUserId();

        Optional<User> optionalSender = userService.get(senderId);
        User sender;
        // НЕ РЕФАКТОРИТЬ!!! TODO
        // См. возможные причины в OneCoffeeUpdateHandler::map(MessageCreatedUpdate)
        sender = optionalSender.orElseGet(() -> new User(senderId, "Cyberpunk2077", UserState.DEFAULT, message.getSender().getUsername()));
        sender.setState(UserState.WAITING);
        userService.save(sender);
        messageSender.sendMessage(message.getSender().getUserId(),
                "Вы успешно добавлены в список ждущий пользователей! Ожидайте начала диалога! ");
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/test")
    private StateResult testt(Message message) {
        messageSender.sendKeyboard(message.getSender().getUserId(), new TestYesNoKeyBoard());
        return new StateResult(StateResult.ResultState.SUCCESS);
    }
}
