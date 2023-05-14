package one.coffee.commands.handlers;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AudioAttachment;
import chat.tamtam.botapi.model.ContactAttachment;
import chat.tamtam.botapi.model.FileAttachment;
import chat.tamtam.botapi.model.InlineKeyboardAttachment;
import chat.tamtam.botapi.model.LocationAttachment;
import chat.tamtam.botapi.model.Message;
import chat.tamtam.botapi.model.PhotoAttachment;
import chat.tamtam.botapi.model.ShareAttachment;
import chat.tamtam.botapi.model.StickerAttachment;
import chat.tamtam.botapi.model.VideoAttachment;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.ParentClasses.Result;
import one.coffee.antispam.DetectPornService;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.sql.states.UserConnectionState;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user_connection.UserConnection;
import one.coffee.sql.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ChattingStateHandler extends StateHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private DetectPornService detectPornService;

    @Override
    public UserState getHandlingState() {
        return UserState.CHATTING;
    }

    @Override
    protected StateResult handleText(Message message) {
        User recipient = getRecipient(message);
        if (recipient == null) {
            return new StateResult(Result.ResultState.ERROR, "Got null user : " + message.getSender());
        }

        List<Attachment> attachments = message.getBody().getAttachments();
        AtomicBoolean hasPorn = new AtomicBoolean(false);
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                if (hasPorn.get()) {
                    break;
                }
                if (attachment.getType().equals(Attachment.IMAGE)) {
                    attachment.visit(new Attachment.Visitor() {
                        @Override
                        public void visit(PhotoAttachment photoAttachment) {
                            try {
                                hasPorn.set(detectPornService.hasPornOnImage(photoAttachment.getPayload().getUrl()));
                            } catch (IOException | InterruptedException e) {
                                LOG.error("Error while detecting porn", e);
                            }
                        }

                        @Override
                        public void visit(VideoAttachment videoAttachment) {

                        }

                        @Override
                        public void visit(AudioAttachment audioAttachment) {

                        }

                        @Override
                        public void visit(FileAttachment fileAttachment) {

                        }

                        @Override
                        public void visit(StickerAttachment stickerAttachment) {

                        }

                        @Override
                        public void visit(ContactAttachment contactAttachment) {

                        }

                        @Override
                        public void visit(InlineKeyboardAttachment inlineKeyboardAttachment) {

                        }

                        @Override
                        public void visit(ShareAttachment shareAttachment) {

                        }

                        @Override
                        public void visit(LocationAttachment locationAttachment) {

                        }

                        @Override
                        public void visitDefault(Attachment attachment) {

                        }
                    });
                }
            }
        }

        if (hasPorn.get()) {
            messageSender.sendMessage(
                    recipient.getId(),
                    NewMessageBodyBuilder.ofText("Вам отправили фото неприличного содержания - мы его заблокировали, чтобы вам было комфортно").build()
            );
        } else {
            messageSender.sendMessage(
                    recipient.getId(),
                    NewMessageBodyBuilder.copyOf(message).build()
            );
        }
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/approve")
    private StateResult handleApprove(Message message) {
        long senderId = message.getSender().getUserId();
        Optional<UserConnection> userConnectionOptional = getInProgressConnection(senderId);
        if (userConnectionOptional.isEmpty()) {
            return null;
        }
        UserConnection userConnection = userConnectionOptional.get();
        setApprove(senderId, userConnection);
        processApprove(senderId, userConnection);
        userConnectionService.save(userConnection);
        return new StateResult(Result.ResultState.SUCCESS);
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
        messageSender.sendMessage(
                senderId,
                "Вы подтвердили свою симпатию к собеседнику! Ожидайте, пока он примет решение"
        );
        userConnectionService.getConnectedUser(senderId).ifPresentOrElse(connectedUser ->
                messageSender.sendMessage(
                        connectedUser.getId(),
                        "Ваш собеседник проявил к Вам интерес! Ответьте взаимностью или прервите переписку"
                ), () -> {
        });
    }

    private void sendContactInfo(long senderId, User recipient) {
        messageSender.sendMessage(
                senderId,
                "Вы понравились Вашему собеседнику," +
                        " поэтому он решил поделиться с Вами своими контактами: " + recipient.getUserInfo()
        );
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(),
                """
                        Список команд бота, доступных для использования:
                        /help - список всех команд
                        /end - закончить диалог с пользователем
                        """);
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/end")
    private StateResult handleEnd(Message message) {
        User recipient = getRecipient(message);
        if (recipient == null) {
            return null;
        }

        long senderId = message.getSender().getUserId();
        Optional<UserConnection> userConnectionOptional = getInProgressConnection(senderId);
        if (userConnectionOptional.isEmpty()) {
            return new StateResult(Result.ResultState.ERROR, "userConnectionOptional.isEmpty()");
        }
        UserConnection userConnection = userConnectionOptional.get();

        userConnection.setState(UserConnectionState.UNSUCCESSFUL);
        userConnectionService.delete(userConnection);

        messageSender.sendMessage(
                senderId,
                "Диалог с пользователем завершён"
        );
        messageSender.sendMessage(
                recipient.getId(),
                "Пользователь решил закончить с вами диалог, надеюсь, все прошло сладко!"
        );
        return new StateResult(Result.ResultState.SUCCESS);
    }

    private User getRecipient(Message message) {
        long senderId = message.getSender().getUserId();
        long recipientId = userConnectionService.getConnectedUserId(senderId);
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
            LOG.error("The recipient " + recipient + " is not in chatting state for user " + senderId);
            handleConnectionError(message);
            return null;
        }

        if (userConnectionService.haveNotConnection(recipientId, senderId)) {
            LOG.error("The recipient " + recipient + " is chatting with other person, not with " + senderId);
            handleConnectionError(message);
            return null;
        }

        return recipient;
    }

    private void handleConnectionError(Message message) {
        long senderId = message.getSender().getUserId();
        messageSender.sendMessage(senderId, "Похоже соединение разорвалось...");
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

    private Optional<UserConnection> getInProgressConnection(long senderId) {
        return Optional.ofNullable(userConnectionService.getInProgressConnectionByUserId(senderId).orElseGet(() -> {
            messageSender.sendMessage(
                    senderId,
                    "Не могу найти Вашего собеседника! Видимо, он решил поиграть в прятки..."
            );
            LOG.warn("Can't handle: No such user connection for sender {}", senderId);
            return null;
        }));
    }
}
