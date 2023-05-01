package one.coffee.bot;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import chat.tamtam.bot.updates.NoopUpdateVisitor;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.Message;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import one.coffee.commands.ChattingCommandHandler;
import one.coffee.commands.DefaultCommandHandler;
import one.coffee.commands.WaitingCommandHandler;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.sql.utils.UserState;
import one.coffee.utils.MessageSender;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneCoffeeBotUpdateHandler extends NoopUpdateVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserService userService = StaticContext.USER_SERVICE;
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
        User user = User.build()
                .setUsername(model.getUser().getUsername())
                .get();
        userService.save(user);
        messageSender.sendMessage(
                userId,
                """
                        Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе
                        Напиши /help, чтобы получить список команд!
                        """);
    }

    @Override
    public void visit(MessageCreatedUpdate update) {
        Message updateMessage = update.getMessage();
        long userId = Objects.requireNonNull(updateMessage.getSender().getUserId(), "UserId is null");

        User user = userService
                .get(userId)
                .orElseGet(() -> /*TODO NULL_USER*/ SQLUtils.recoverSender(updateMessage).get());

        UserState userState = user.getState();
        switch (userState) {
            case DEFAULT -> defaultHandler.handle(update.getMessage());
            case WAITING -> waitingHandler.handle(update.getMessage());
            case CHATTING -> chattingHandler.handle(update.getMessage());
            default -> {
                LOG.warn("State {} for user with 'id' = {} is not supported", userState, userId);
                user.setState(UserState.DEFAULT);
                userService.save(user);
                messageSender.sendMessage(
                        userId,
                        "Оххх... Что-то ошибочка вышла... Попробуйте еще раз"
                );
            }
        }
    }

}
