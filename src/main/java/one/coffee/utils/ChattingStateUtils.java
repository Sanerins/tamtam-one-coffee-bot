package one.coffee.utils;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.keyboards.InConversationKeyboard;
import one.coffee.sql.states.UserConnectionState;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.utils.SQLUtils;

@Component
public class ChattingStateUtils extends StateUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public StateResult handleApprove(long userId, String username) {
        Optional<UserConnection> userConnectionOptional = getInProgressConnection(userId);
        if (userConnectionOptional.isEmpty()) {
            return null;
        }
        UserConnection userConnection = userConnectionOptional.get();
        setApprove(userId, userConnection);
        processApprove(userId, userConnection);
        userConnectionService.save(userConnection);
        return new StateResult(Result.ResultState.SUCCESS);
    }

    public StateResult handleEnd(long userId, String username) {
        User recipient = getRecipient(userId, username);
        if (recipient == null) {
            return null;
        }

        Optional<UserConnection> userConnectionOptional = getInProgressConnection(userId);
        if (userConnectionOptional.isEmpty()) {
            return new StateResult(Result.ResultState.ERROR, "userConnectionOptional.isEmpty()");
        }
        UserConnection userConnection = userConnectionOptional.get();

        userConnection.setState(UserConnectionState.UNSUCCESSFUL);
        userConnectionService.delete(userConnection);

        messageSender.sendKeyboard(
                userId,
                new DefaultStateKeyboard("Диалог с пользователем завершён")
        );
        messageSender.sendKeyboard(
                recipient.getId(),
                new DefaultStateKeyboard("Пользователь решил закончить с вами диалог, надеюсь, все прошло сладко!")
        );
        return new StateResult(Result.ResultState.SUCCESS);
    }

    public User getRecipient(long userId, String username) {
        long recipientId = userConnectionService.getConnectedUserId(userId);
        if (recipientId == SQLUtils.DEFAULT_ID) {
            return null;
        }

        Optional<User> optionalRecipient = userService.get(recipientId);
        User recipient;
        if (optionalRecipient.isEmpty()) { // TODO Восстановление инфы
            recipient = new User(recipientId, "Cyberpunk2077", UserState.DEFAULT, "Вася Пупкин");
            userService.save(recipient);
        } else {
            recipient = optionalRecipient.get();
        }

        if (recipient.isNotChatting()) {
            LOG.error("The recipient " + recipient + " is not in chatting state for user " + userId);
            handleConnectionError(userId, username);
            return null;
        }

        if (userConnectionService.haveNotConnection(recipientId, userId)) {
            LOG.error("The recipient " + recipient + " is chatting with other person, not with " + userId);
            handleConnectionError(userId, username);
            return null;
        }

        return recipient;
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

        userConnectionService.delete(userConnection);
    }

    private void processHalfApprove(long senderId) {
        messageSender.sendMessage(
                senderId,
                "Вы подтвердили свою симпатию к собеседнику! Ожидайте, пока он примет решение"
        );
        userConnectionService.getConnectedUser(senderId).ifPresentOrElse(connectedUser ->
                messageSender.sendKeyboard(connectedUser.getId(),
                        new InConversationKeyboard("Ваш собеседник проявил к Вам интерес! Ответьте взаимностью, " +
                                "прервите переписку или просто продолжите общение, если еще не уверены")
                ), () -> {
        });
    }

    private void sendContactInfo(long senderId, User recipient) {
        messageSender.sendMessage(
                senderId,
                "Вы понравились Вашему собеседнику," +
                        " поэтому он решил поделиться с Вами своими контактами: " + recipient.getUserInfo()
        );
        messageSender.sendKeyboard(senderId,
                new DefaultStateKeyboard("Теперь вам осталось встретиться с друг другом очно за чашечкой кофе! " +
                        "Договоритесь об этом по полученным контактам!\n"));
    }

    private void handleConnectionError(long userId, String username) {
        messageSender.sendMessage(userId, """
                Похоже соединение разорвалось...
                /help - для списка команд в этот тяжелый момент
                """);
        Optional<User> optionalSender = userService.get(userId);
        User sender;
        if (optionalSender.isEmpty()) { // TODO Восстановление инфы
            sender = new User(userId, "Cyberpunk2077", UserState.DEFAULT, username);
            userService.save(sender);
        } else {
            sender = optionalSender.get();
        }
        sender.setState(UserState.DEFAULT);
        userService.save(sender);
    }

    private Optional<UserConnection> getInProgressConnection(long senderId) {
        return Optional.ofNullable(userConnectionService.getInProgressConnectionByUserId(senderId).orElseGet(() -> {
            messageSender.sendMessage(
                    senderId,
                    """
                            Не могу найти Вашего собеседника! Видимо, он решил поиграть в прятки...
                            /help - для списка команд в этот тяжелый момент
                            """
            );
            LOG.warn("Can't handle: No such user connection for sender {}", senderId);
            return null;
        }));
    }

}
