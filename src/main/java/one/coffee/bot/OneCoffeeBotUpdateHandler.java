package one.coffee.bot;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import one.coffee.sql.entities.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.updates.NoopUpdateVisitor;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import one.coffee.commands.ChattingCommandHandler;
import one.coffee.commands.DefaultCommandHandler;
import one.coffee.commands.WaitingCommandHandler;
import one.coffee.utils.MessageSender;
import one.coffee.utils.StaticContext;

public class OneCoffeeBotUpdateHandler extends NoopUpdateVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MessageSender messageSender = StaticContext.getMessageSender();
    private final ConcurrentMap<Long, UserState.StateType> userStateMap = StaticContext.getUserStateMap();

    private final DefaultCommandHandler defaultHandler;
    private final ChattingCommandHandler chattingHandler;
    private final WaitingCommandHandler waitingHandler;

    public OneCoffeeBotUpdateHandler() {
        super();
        defaultHandler = new DefaultCommandHandler();
        chattingHandler = new ChattingCommandHandler();
        waitingHandler = new WaitingCommandHandler();
    }

    @Override
    public void visit(BotStartedUpdate model) {
        Long userId = Objects.requireNonNull(model.getUser().getUserId(), "UserId is null");
        userStateMap.put(userId, UserState.StateType.DEFAULT);
        messageSender.sendMessage(userId,
                NewMessageBodyBuilder.ofText("Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе \n" +
                        "Напиши /help, чтобы получить список команд!").build());
    }

    @Override
    public void visit(MessageCreatedUpdate update) {
        Long userId = Objects.requireNonNull(update.getMessage().getSender().getUserId(), "UserId is null");
        UserState.StateType state = userStateMap.get(userId);
        if (state == null) {
            LOG.warn("State of user: " + userId + " is null while supposed not to be");
            userStateMap.put(userId, UserState.StateType.DEFAULT);
            state = UserState.StateType.DEFAULT;
        }
        switch (state) {
            case DEFAULT -> {
                defaultHandler.handle(update.getMessage());
            }
            case WAITING -> {
                waitingHandler.handle(update.getMessage());
            }
            case CHATTING -> {
                chattingHandler.handle(update.getMessage());
            }
            default -> {
                LOG.warn("State " + state.name() + " of user: " + userId + " is not supported");
                messageSender.sendMessage(userId,
                        NewMessageBodyBuilder.ofText("Оххх... Что-то ошибочка вышла... Попробуйте еще раз").build());
                userStateMap.put(userId, UserState.StateType.DEFAULT);
            }
        }
    }

}
