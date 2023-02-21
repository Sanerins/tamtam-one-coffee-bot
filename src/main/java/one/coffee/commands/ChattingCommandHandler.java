package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

public class ChattingCommandHandler extends CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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

        messageSender.sendMessage(recipient.getUserId(), NewMessageBodyBuilder.copyOf(message).build());
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


        try {
            UserConnection userConnection = UserConnectionsTable.getUserConnectionByUserId(senderId);
            userConnection.breakConnection();

            messageSender.sendMessage(
                    senderId,
                    NewMessageBodyBuilder.ofText("Диалог с пользователем завершён").build()
            );
            messageSender.sendMessage(
                    recipient.getUserId(),
                    NewMessageBodyBuilder.ofText("Пользователь решил закончить с вами диалог, надеюсь все прошло сладко!").build()
            );
        } catch (SQLException e) {
            LOG.error("Connection with user " + senderId + " has already broken!"); // TODO Кем?
            messageSender.sendMessage(
                    senderId,
                    NewMessageBodyBuilder.ofText("Не получилось отсоединиться от работяги((( Попробуй снова").build()
            );
        }
    }

    private User getRecipient(Message message) {
        try {
            User recipient = UsersTable.getUserByUserId(
                    UsersTable.getUserByUserId(
                            message.getSender().getUserId()
                    ).getConnectedUserId()
            );

            if (recipient.getStateId() != UserState.CHATTING.getStateId()) {
                LOG.error("The recipient " + recipient + " is not in chatting state for user " + message.getSender().getUserId());
                handleConnectionError(message);
                return null;
            }

            if (recipient.getConnectedUserId() != message.getSender().getUserId()) {
                LOG.error("The recipient " + recipient + " is chatting with other person, not with " + message.getSender().getUserId());
                handleConnectionError(message);
                return null;
            }

            return recipient;
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            handleConnectionError(message);
            return null;
        }
    }

    private void handleConnectionError(Message message) {
        long senderId = message.getSender().getUserId();

        try {
            messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText("Похоже соединение разорвалось...").build());
            User sender = UsersTable.getUserByUserId(senderId);
            sender.setStateId(UserState.DEFAULT.getId());
            sender.commit();
        } catch (SQLException e) {
            LOG.error("The sender " + senderId + " was deleted from DB!"); // TODO Предложения?
        }
    }

}
