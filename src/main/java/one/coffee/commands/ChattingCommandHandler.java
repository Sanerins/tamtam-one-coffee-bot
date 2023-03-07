package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.UserState;
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

        try {
            UserConnection userConnection = UserConnectionsTable.getUserConnectionUserById(senderId);
            userConnection.breakConnection();

            messageSender.sendMessage(
                    senderId,
                    NewMessageBodyBuilder.ofText("Диалог с пользователем завершён").build()
            );
            messageSender.sendMessage(
                    recipient.getId(),
                    NewMessageBodyBuilder.ofText("Пользователь решил закончить с вами диалог, надеюсь все прошло сладко!").build()
            );
        } catch (SQLException e) {
            // Тут исключение может упасть по трём причинам
            // 1. Упала база;
            // 2. Невалидный запрос.
            // Действия:
            // 1. Описано в OneCoffeeBotUpdateHandler::visit(MessageCreatedUpdate)
            // 2. Описано в OneCoffeeBotUpdateHandler::visit(MessageCreatedUpdate)
            LOG.error("Can't break connection for user {}!", senderId, e);
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

            if (recipient.getState() != UserState.CHATTING) {
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
            LOG.error("Can't get recipient!", e);
            handleConnectionError(message);
            return null;
        }
    }

    private void handleConnectionError(Message message) {
        long senderId = message.getSender().getUserId();

        try {
            messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText("Похоже соединение разорвалось...").build());
            User sender = UsersTable.getUserByUserId(senderId);
            sender.setState(UserState.DEFAULT);
            sender.commit();
        } catch (SQLException e) {
            // Тут исключение может упасть по трём причинам
            // 1. Упала база;
            // 2. Невалидный запрос;
            // 3. Отправителя нет в базе.
            // Действия:
            // 1. Описано в OneCoffeeBotUpdateHandler::visit(MessageCreatedUpdate)
            // 2. Описано в OneCoffeeBotUpdateHandler::visit(MessageCreatedUpdate)
            // 3. Надо восстановить работягу в базе. Но для этого потребуется запросить его данные снова.
            LOG.warn("Can't process error for user {}!", senderId, e);
            // TODO Тут сделать механизм опроса пользователя об актуальной информации, так как старую мы потеряли(((
            messageSender.sendMessage(senderId,
                    NewMessageBodyBuilder.ofText("Работяга! Информация о тебе по неясным причинам была удалена." +
                            " Если хочешь продолжать общение, пожалуйста, введи заново сведения о себе.").build());
        }
    }

}
