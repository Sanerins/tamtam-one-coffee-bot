package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

public class ChattingCommandHandler extends CommandHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserService userService = StaticContext.USER_SERVICE;
    private static final UserConnectionService userConnectionService = StaticContext.USER_CONNECTION_SERVICE;

    public ChattingCommandHandler() {
        super(StaticContext.getMessageSender());
    }

    @Override
    protected void handleCommand(Message message, String[] commandWithArgs) {
        switch (commandWithArgs[0]) {
            case ("/help") -> {
                handleHelp(message);
            }
            case ("/end") -> {
                handleEnd(message);
            }
            default -> {
                handleDefault(message);
            }
        }
    }

    @Override
    protected void handleText(Message message) {
        User recipient = getRecipient(message);
        if (recipient == null) {
            return;
        }

        messageSender.sendMessage(recipient.getId(), NewMessageBodyBuilder.copyOf(message).build());
    }

    private void handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(), NewMessageBodyBuilder.ofText("""
                Список команд бота, доступных для использования:
                /help - список всех команд
                /end - закончить диалог с пользователем""").build());
    }

    private void handleEnd(Message message) {
        User recipient = getRecipient(message);
        if (recipient == null) {
            return;
        }
        long senderId = message.getSender().getUserId();

        UserConnection userConnection = userConnectionService.getByUserId(senderId).get();
        userConnectionService.delete(userConnection);

        messageSender.sendMessage(
                senderId,
                NewMessageBodyBuilder.ofText("Диалог с пользователем завершён").build()
        );
        messageSender.sendMessage(
                recipient.getId(),
                NewMessageBodyBuilder.ofText("Пользователь решил закончить с вами диалог, надеюсь все прошло сладко!").build()
        );
    }

    private User getRecipient(Message message) {
        long recipientId = userConnectionService.getConnectedUserId(message.getSender().getUserId());
        if (recipientId == SQLUtils.NO_ID) {
            return null;
        }
        User recipient = userService.get(recipientId).get();

        if (recipient.getState() != UserState.CHATTING) {
            LOG.error("The recipient " + recipient + " is not in chatting state for user " + message.getSender().getUserId());
            handleConnectionError(message);
            return null;
        }

        if (userConnectionService.getConnectedUserId(recipientId) != message.getSender().getUserId()) {
            LOG.error("The recipient " + recipient + " is chatting with other person, not with " + message.getSender().getUserId());
            handleConnectionError(message);
            return null;
        }

        return recipient;
    }

    private void handleConnectionError(Message message) {
        long senderId = message.getSender().getUserId();
        messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText("Похоже соединение разорвалось...").build());
        User sender = userService.get(senderId).get();
        sender.setState(UserState.DEFAULT);
        userService.save(sender);
    }

}
