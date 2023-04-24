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
import java.util.Optional;

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
        UserConnection userConnection = userConnectionService.getByUserId(senderId).get();
        if (userConnection.getUser1Id() == senderId) {
            userConnection.setApprove1(true);
        } else {
            userConnection.setApprove2(true);
        }
        userConnectionService.save(userConnection);
        boolean allApprove = userConnection.isApprove1() && userConnection.isApprove2();
        if (allApprove) {
            processAllApprove(senderId);
        }
    }

    private void processAllApprove(long senderId) {
        User recipient = userService.get(userConnectionService.getConnectedUserId(senderId)).get();
        User sender = userService.get(senderId).get();

        sendContactInfo(senderId, recipient);
        sendContactInfo(recipient.getId(), sender);
    }

    private void sendContactInfo(long senderId, User recipient) {
        messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText("Вы понравились Вашему собеседнику, поэтому он решил поделиться с Вами своими контактами:").build());
        String username = "К сожалению, собеседник не заполнил имя пользователя";
        if (recipient.getUsername() != null) {
            username = recipient.getUsername();
        }
        messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText(username).build());
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
        Optional<User> senderOpt = userService.get(senderId);
        if (senderOpt.isEmpty()) {
            LOG.warn("Can't handleEnd because sender is null");
            return;
        }
        User sender = senderOpt.get();

        recipient.setState(UserState.WAITING);
        sender.setState(UserState.DEFAULT);

        userService.save(recipient);
        userService.save(sender);

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
        long senderId = message.getSender().getUserId();
        long recipientId = userConnectionService.getConnectedUserId(senderId);
        if (recipientId == SQLUtils.NO_ID) {
            return null;
        }

        Optional<User> optionalRecipient = userService.get(recipientId);
        User recipient;
        if (optionalRecipient.isEmpty()) { // TODO Восстановление инфы
            recipient = new User(recipientId, "Cyberpunk2077", UserState.DEFAULT, null);
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
        messageSender.sendMessage(senderId, NewMessageBodyBuilder.ofText("Похоже соединение разорвалось...").build());
        Optional<User> optionalSender = userService.get(senderId);
        User sender;
        if (optionalSender.isEmpty()) { // TODO Восстановление инфы
            sender = new User(senderId, "Cyberpunk2077", UserState.DEFAULT, message.getSender().getUsername());
            userService.save(sender);
        } else {
            sender = optionalSender.get();
        }
        sender.setState(UserState.DEFAULT);
        userService.save(sender);
    }

}
