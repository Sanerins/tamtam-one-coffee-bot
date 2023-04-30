package one.coffee.commands;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
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
            case ("/help") -> handleHelp(message);
            case ("/end") -> handleEnd(message);
            case ("/approve") -> handleApprove(message);
            default -> handleDefault(message);
        }
    }

    private void handleApprove(Message message) {
        long senderId = message.getSender().getUserId();
        Optional<UserConnection> userConnectionOptional = getInProgressConnection(senderId);
        if (userConnectionOptional.isEmpty()) {
            return;
        }
        UserConnection userConnection = userConnectionOptional.get();
        setApprove(senderId, userConnection);
        processApprove(senderId, userConnection);
        userConnectionService.save(userConnection);
    }

    private void setApprove(long senderId, UserConnection userConnection) {
        if (userConnection.getUser1Id() == senderId) {
            userConnection.setApprove1(true);
        } else {
            userConnection.setApprove2(true);
        }
    }

    private void processApprove(long senderId, UserConnection userConnection) {
        if (userConnection.isAllApprove()) {
            processAllApprove(senderId, userConnection);
        } else {
            processHalfApprove(senderId);
        }
    }

    private void processAllApprove(long senderId, UserConnection userConnection) {
        User recipient = userConnectionService.getConnectedUser(senderId).get();
        User sender = userService.get(senderId).get();

        sendContactInfo(senderId, recipient);
        sendContactInfo(recipient.getId(), sender);

        userConnection.setState(UserConnectionState.SUCCESSFUL);
    }

    private void processHalfApprove(long senderId) {
        messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText(
                "Вы подтвердили свою симпатию к собеседнику! Ожидайте, пока он примет решение"
        ).build());
        userConnectionService.getConnectedUser(senderId).ifPresentOrElse(connectedUser ->
                messageSender.sendMessage(connectedUser.getId(), NewMessageBodyBuilder.ofText(
                "Ваш собеседник проявил к Вам интерес! Ответьте взаимностью или прервите переписку"
        ).build()), () -> {});
    }

    private void sendContactInfo(long senderId, User recipient) {
        messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText(
                "Вы понравились Вашему собеседнику," +
                        " поэтому он решил поделиться с Вами своими контактами: " + recipient.getUserInfo()
        ).build());
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
                /end - закончить диалог с пользователем
                """).build());
    }

    private void handleEnd(Message message) {
        User recipient = getRecipient(message);
        if (recipient == null) {
            return;
        }

        long senderId = message.getSender().getUserId();
        Optional<UserConnection> userConnectionOptional = getInProgressConnection(senderId);
        if (userConnectionOptional.isEmpty()) {
            return;
        }
        UserConnection userConnection = userConnectionOptional.get();

        userConnection.setState(UserConnectionState.UNSUCCESSFUL);
        StaticContext.USER_CONNECTION_SERVICE.delete(userConnection);

        messageSender.sendMessage(
                senderId,
                NewMessageBodyBuilder.ofText("Диалог с пользователем завершён").build()
        );
        messageSender.sendMessage(
                recipient.getId(),
                NewMessageBodyBuilder.ofText("Пользователь решил закончить с вами диалог, надеюсь, все прошло сладко!").build()
        );
    }

    private User getRecipient(Message message) {
        long senderId = message.getSender().getUserId();
        long recipientId = userConnectionService.getConnectedUserId(senderId);
        if (recipientId == SQLUtils.DEFAULT_ID) {
            return null;
        }

        Optional<User> optionalRecipient = userService.get(recipientId);
        User recipient;
        //TODO NULL_USER
        if (optionalRecipient.isEmpty()) {
            recipient = User.build().setId(recipientId).get();
            userService.save(recipient);
        } else {
            recipient = optionalRecipient.get();
        }

        if (recipient.getState() != UserState.CHATTING) {
            LOG.error("The recipient " + recipient + " is not in chatting state for user " + senderId);
            handleConnectionError(message);
            return null;
        }

        if (userConnectionService.getConnectedUserId(recipientId) != message.getSender().getUserId()) {
            LOG.error("The recipient " + recipient + " is chatting with other person, not with " + senderId);
            handleConnectionError(message);
            return null;
        }

        return recipient;
    }

    private void handleConnectionError(Message message) {
        long senderId = message.getSender().getUserId();
        messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText(
                "Похоже соединение разорвалось..."
        ).build());
        User sender = userService.get(senderId).orElseGet(() -> /*TODO NULL_USER*/ SQLUtils.recoverSender(message));
        sender.setState(UserState.DEFAULT);
        userService.save(sender);
    }

    private Optional<UserConnection> getInProgressConnection(long senderId) {
        Optional<UserConnection> userConnectionOptional =
                userConnectionService.getInProgressConnectionByUserId(senderId);
        if (userConnectionOptional.isEmpty()) {
            messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText(
                    "Не могу найти Вашего собеседника! Видимо, он решил поиграть в прятки..."
            ).build());
            LOG.warn("Can't handle: No such user connection for sender {}", senderId);
            return Optional.empty();
        }
        return userConnectionOptional;
    }

}
