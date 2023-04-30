package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.utils.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

public class DefaultCommandHandler extends CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserService userService = StaticContext.USER_SERVICE;
    private static final UserConnectionService userConnectionService = StaticContext.USER_CONNECTION_SERVICE;

    public DefaultCommandHandler() {
        super(StaticContext.getMessageSender());
    }

    @Override
    protected void handleCommand(Message message, String[] commandWithArgs) {
        switch (commandWithArgs[0]) {
            case ("/help") -> {
                handleHelp(message);
            }
            case ("/start") -> {
                handleStart(message);
            }
            default -> {
                handleDefault(message);
            }
        }
    }

    private void handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(), NewMessageBodyBuilder.ofText("""
                Список команд бота, доступных для использования:
                /help - список всех команд
                /start - начать диалог с пользователем""").build());
    }

    private void handleStart(Message message) {
        long senderId = message.getSender().getUserId();
        Optional<User> optionalSender = userService.get(senderId);
        User sender;
        if (optionalSender.isEmpty()) {
            // См. возможные причины в OneCoffeeUpdateHandler::visit(MessageCreatedUpdate)
            sender = new User(senderId, "Cyberpunk2077", UserState.DEFAULT, message.getSender().getUsername());
            userService.save(sender);
        } else {
            sender = optionalSender.get();
        }

        List<User> userWaitList = userService.getWaitingUsers(1);
        if (userWaitList.isEmpty()) {
            startTheWait(message);
            return;
        }

        User recipient = userWaitList.get(0);
        UserConnection userConnection = new UserConnection(senderId, recipient.getId());
        userConnectionService.save(userConnection);

        messageSender.sendMessage(senderId,
                NewMessageBodyBuilder.ofText("""
                            Я нашел вам собеседника!
                            Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                            Список команд, доступных во время беседы можно открыть на /help\s""").build());
        messageSender.sendMessage(recipient.getId(),
                NewMessageBodyBuilder.ofText("""
                            Я нашел вам собеседника!
                            Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                            Список команд, доступных во время беседы можно открыть на /help\s""").build());
    }

    private void startTheWait(Message message) {
        long senderId = message.getSender().getUserId();

        Optional<User> optionalSender = userService.get(senderId);
        User sender;
        if (optionalSender.isEmpty()) { // НЕ РЕФАКТОРИТЬ!!! TODO
            // См. возможные причины в OneCoffeeUpdateHandler::visit(MessageCreatedUpdate)
            sender = new User(senderId, "Cyberpunk2077", UserState.DEFAULT, message.getSender().getUsername());
        } else {
            sender = optionalSender.get();
        }
        sender.setState(UserState.WAITING);
        userService.save(sender);
        messageSender.sendMessage(message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Вы успешно добавлены в список ждущий пользователей! Ожидайте начала диалога! ").build());
    }

}
