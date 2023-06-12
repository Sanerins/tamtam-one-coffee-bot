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
import one.coffee.keyboards.InConversationKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.WaitingKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.utils.ChattingStateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ChattingStateHandler extends StateHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private DetectPornService detectPornService;

    @Autowired
    private ChattingStateUtils chattingUtils;

    @Override
    public UserState getHandlingState() {
        return UserState.CHATTING;
    }

    @Override
    protected StateResult handleText(Message message) {
        User recipient = chattingUtils.getRecipient(message.getSender().getUserId(), message.getSender().getUsername());
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
                    "Вам отправили фото неприличного содержания - мы его заблокировали, чтобы вам было комфортно"
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
        return chattingUtils.handleApprove(message.getSender().getUserId(), message.getSender().getUsername());
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/end")
    public StateResult handleEnd(Message message) {
        return chattingUtils.handleEnd(message.getSender().getUserId(), message.getSender().getUsername());
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendKeyboard(message.getSender().getUserId(), new InConversationKeyboard("""
                        Список команд бота, доступных для использования:
                        /approve - подтвердить желание поделиться контактной информацией
                        /end - закончить диалог с пользователем
                        Или же используйте кнопки:
                        """));
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new InConversationKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }
}
