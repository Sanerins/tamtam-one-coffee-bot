package one.coffee.bot;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.updates.NoopUpdateVisitor;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import one.coffee.commands.ChattingCommandHandler;
import one.coffee.commands.DefaultCommandHandler;
import one.coffee.commands.WaitingCommandHandler;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UsersTable;
import one.coffee.utils.MessageSender;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;
import java.util.Objects;

public class OneCoffeeBotUpdateHandler extends NoopUpdateVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MessageSender messageSender = StaticContext.getMessageSender();

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
        long userId = Objects.requireNonNull(model.getUser().getUserId(), "UserId is null");

        try {
            User user = new User(userId, "Cyberpunk2077", UserState.DEFAULT.getStateId());
            messageSender.sendMessage(userId,
                    NewMessageBodyBuilder.ofText("Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе \n" +
                            "Напиши /help, чтобы получить список команд!").build());
        } catch (SQLException e) {
            LOG.warn("Can't update user state in user " + userId);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(MessageCreatedUpdate update) {
        try {
            long userId = Objects.requireNonNull(update.getMessage().getSender().getUserId(), "UserId is null");
            User user = UsersTable.getUserByUserId(userId);
            UserState.StateType stateType = UserState.StateType.fromId(user.getStateId());
            switch (stateType) {
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
                    LOG.warn("State " + stateType + " of user: " + userId + " is not supported");
                    messageSender.sendMessage(userId,
                            NewMessageBodyBuilder.ofText("Оххх... Что-то ошибочка вышла... Попробуйте еще раз").build());
                    user.setStateId(UserState.DEFAULT.getStateId());
                    user.commit();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
