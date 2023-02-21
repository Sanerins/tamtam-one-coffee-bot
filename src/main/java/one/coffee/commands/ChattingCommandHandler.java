package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.entities.UserState;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

public class ChattingCommandHandler extends CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConcurrentMap<Long, UserState.StateType> userStateMap = StaticContext.getUserStateMap();
    private final ConcurrentMap<Long, Long> userConnections = StaticContext.getConnections();

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
        Long recipient = getRecipient(message);
        if (recipient == null) {
            return;
        }

        messageSender.sendMessage(recipient, NewMessageBodyBuilder.copyOf(message).build());
    }

    private void handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(), NewMessageBodyBuilder.ofText("""
                Список команд бота, доступных для использования:
                /help - список всех команд
                /end - закончить диалог с пользователем""").build());
    }

    private void handleEnd(Message message) {
        Long recipient = getRecipient(message);
        if (recipient == null) {
            return;
        }

        messageSender.sendMessage(message.getSender().getUserId(), NewMessageBodyBuilder.ofText("Заканчиваю диалог с пользователем...").build());
        messageSender.sendMessage(recipient, NewMessageBodyBuilder.ofText("Пользователь решил закончить с вами диалог, надеюсь все прошло сладко!").build());

        userStateMap.put(message.getSender().getUserId(), UserState.StateType.DEFAULT);
        userStateMap.put(recipient, UserState.StateType.DEFAULT);

        userConnections.remove(message.getSender().getUserId());
        userConnections.remove(recipient);
    }

    private Long getRecipient(Message message) {
        Long recipient = userConnections.get(message.getSender().getUserId());

        if (recipient == null) {
            LOG.error("The recipient can't be found for user " + message.getSender().getUserId());
            handleConnectionError(message);
            return null;
        }

        if (userStateMap.get(recipient) != UserState.StateType.CHATTING) {
            LOG.error("The recipient " + recipient + " is not in chatting state for user " + message.getSender().getUserId());
            handleConnectionError(message);
            return null;
        }
        if (!Objects.equals(userConnections.get(recipient), message.getSender().getUserId())) {
            LOG.error("The recipient " + recipient + " is chatting with other person, not with " + message.getSender().getUserId());
            handleConnectionError(message);
            return null;
        }
        return recipient;
    }

    private void handleConnectionError(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(), NewMessageBodyBuilder.ofText("Похоже соединение разорвалось...").build());
        userStateMap.put(message.getSender().getUserId(), UserState.StateType.DEFAULT);
    }

}
