package one.coffee.commands;

import chat.tamtam.bot.annotations.CommandHandler;
import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.bot.UpdateResult;
import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user_connection.UserConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Component
public class DefaultStateHandler extends StateHandler {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public UserState getHandlingState() {
        return UserState.DEFAULT;
    }

    @CommandHandler("/help")
    private UpdateResult handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(), NewMessageBodyBuilder.ofText("""
                Список команд бота, доступных для использования:
                /help - список всех команд
                /start - начать диалог с пользователем""").build());
        return new UpdateResult(UpdateResult.UpdateState.SUCCESS);
    }

    @CommandHandler("/start")
    private UpdateResult handleStart(Message message) {
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
            return new UpdateResult(UpdateResult.UpdateState.SUCCESS);
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
        return new UpdateResult(UpdateResult.UpdateState.SUCCESS);
    }

    private void startTheWait(Message message) {
        long senderId = message.getSender().getUserId();

        Optional<User> optionalSender = userService.get(senderId);
        User sender;
        // НЕ РЕФАКТОРИТЬ!!! TODO
        // См. возможные причины в OneCoffeeUpdateHandler::map(MessageCreatedUpdate)
        sender = optionalSender.orElseGet(() -> new User(senderId, "Cyberpunk2077", UserState.DEFAULT, message.getSender().getUsername()));
        sender.setState(UserState.WAITING);
        userService.save(sender);
        messageSender.sendMessage(message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Вы успешно добавлены в список ждущий пользователей! Ожидайте начала диалога! ").build());
    }

}
