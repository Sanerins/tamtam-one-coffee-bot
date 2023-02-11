package one.coffee.commands;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;
import one.coffee.utils.UserState;

public class DefaultCommandHandler extends CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConcurrentMap<Long, UserState> userStateMap = StaticContext.getUserStateMap();
    private final ConcurrentMap<Long, Long> userConnections = StaticContext.getConnections();
    private final BlockingQueue<Long> userWaitList = StaticContext.getUserWaitList();

    public DefaultCommandHandler() {
        super(StaticContext.getMessageSender());
    }

    @Override
    protected void handleCommand(Message message, String[] commandWithArgs) {
        switch (commandWithArgs[0]) {
            case ("/help") -> {
                handleHelp(message);
            }
            case ("/start") -> {
                handleStart(message);
            }
            default -> {
                handleDefault(message);
            }
        }
    }

    private void handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(), NewMessageBodyBuilder.ofText("""
                Список команд бота, доступных для использования:
                /help - список всех команд
                /start - начать диалог с пользователем""").build());
    }

    private void handleStart(Message message) {
        if (userWaitList.isEmpty()) {
            startTheWait(message);
            return;
        }

        Long userId = message.getSender().getUserId();
        Long recipient = null;
        try {
            recipient = userWaitList.poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.warn("Polling user " + userId + " in waiting list was interrupted");
            startTheWait(message);
            return;
        }

        if (recipient == null) {
            LOG.error("Null in the userWaitList");
            startTheWait(message);
            return;
        }
        if (userStateMap.get(recipient) != UserState.WAITING) {
            LOG.error("User " + userWaitList + " is not waiting in the userWaitList");
            startTheWait(message);
            return;
        }

        userConnections.put(recipient, userId);
        userConnections.put(userId, recipient);

        userStateMap.put(recipient, UserState.CHATTING);
        userStateMap.put(userId, UserState.CHATTING);

        messageSender.sendMessage(userId,
                NewMessageBodyBuilder.ofText("""
                        Я нашел вам собеседника!
                        Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                        Список команд, доступных во время беседы можно открыть на /help\s""").build());
        messageSender.sendMessage(recipient,
                NewMessageBodyBuilder.ofText("""
                        Я нашел вам собеседника!
                        Я буду передавать сообщения между вами, можете общаться сколько влезет!)
                        Список команд, доступных во время беседы можно открыть на /help\s""").build());
    }

    private boolean startTheWait(Message message) {
        try {
            userWaitList.put(message.getSender().getUserId());
        } catch (InterruptedException e) {
            LOG.warn("Putting user " + message.getSender().getUserId() + " in waiting list was interrupted");
            messageSender.sendMessage(message.getSender().getUserId(),
                    NewMessageBodyBuilder.ofText("Встать в список ждущих пользователей не получилось, попробуй чуть позже :( ").build());
            return false;
        }
        userStateMap.put(message.getSender().getUserId(), UserState.WAITING);
        messageSender.sendMessage(message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Вы успешно добавлены в список ждущий пользователей! Ожидайте начала диалога! ").build());
        return true;
    }

}
