package one.coffee.commands;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import chat.tamtam.botapi.model.Message;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.sql.utils.UserConnectionState;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            case ("/help") -> handleHelp(message);
            case ("/start") -> handleStart(message);
            default -> handleDefault(message);
        }
    }

    private void handleHelp(Message message) {
        messageSender.sendMessage(
                message.getSender().getUserId(),
                """
                Список команд бота, доступных для использования:
                /help - список всех команд
                /start - начать диалог с пользователем
                """);
    }

    private void handleStart(Message message) {
        SQLUtils.recoverSenderIfAbsent(message);

        long senderId = message.getSender().getUserId();
        Optional<User> chattingCandidateOptional = userService.getChattingCandidate(senderId);
        if (chattingCandidateOptional.isEmpty()) {
            startTheWait(message);
            return;
        }

        User chattingCandidate = chattingCandidateOptional.get();
        UserConnection userConnection = UserConnection.build()
                .setUser1Id(senderId)
                .setUser2Id(chattingCandidate.getId())
                .setState(UserConnectionState.IN_PROGRESS)
                .get();
        userConnectionService.save(userConnection);

        sendStartChattingMessage(senderId);
        sendStartChattingMessage(chattingCandidate.getId());
    }

    private void sendStartChattingMessage(long userId) {
        messageSender.sendMessage(
                userId,
                """
                            Я нашел вам собеседника!
                            Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                            Список команд, доступных во время беседы можно открыть на /help\s
                            """);
    }

    private void startTheWait(Message message) {
        long senderId = message.getSender().getUserId();
        User sender = SQLUtils.recoverSenderIfAbsent(message);
        sender.setState(UserState.WAITING);
        userService.save(sender);
        messageSender.sendMessage(
                senderId,
                "Вы успешно добавлены в список ждущий пользователей! Ожидайте начала диалога!"
        );
    }

}
