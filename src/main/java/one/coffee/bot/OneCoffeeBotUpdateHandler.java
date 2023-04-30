package one.coffee.bot;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.updates.NoopUpdateVisitor;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import one.coffee.commands.ChattingCommandHandler;
import one.coffee.commands.DefaultCommandHandler;
import one.coffee.commands.WaitingCommandHandler;
import one.coffee.sql.utils.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.utils.MessageSender;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

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
        User user = new User(userId, "Cyberpunk2077", UserState.DEFAULT, model.getUser().getUsername());
        userService.save(user);
        messageSender.sendMessage(userId,
                NewMessageBodyBuilder.ofText("Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе \n" +
                        "Напиши /help, чтобы получить список команд!").build());
    }

    @Override
    public void visit(MessageCreatedUpdate update) {
        long userId = Objects.requireNonNull(update.getMessage().getSender().getUserId(), "UserId is null");

        Optional<User> optionalUser = userService.get(userId);
        User user;
        if (optionalUser.isEmpty()) { // НЕ РЕФАКТОРИТЬ!!! TODO Тут будет повтороный опрос пользователя о его данных.
            // Это возможно в двух случаях:
            // 1. Работяга до этого уже переписывался с ботом, разорвал соединение, мы потеряли о нём данные чудесным образом
            // (дропнули или просто почистили таблички, или же хацкер оставил нас у разбитого корыта...), а потом написал /start;
            // 2. Невалидный запрос (на любой стадии).
            // Действия:
            // 1. Пересоздание пользователя (реализуется).
            LOG.warn("No user with id {} in DB! It will be recreated.", userId);
            user = new User(userId, "Cyberpunk2077", UserState.DEFAULT, update.getMessage().getSender().getUsername());
            userService.save(user);
        } else {
            user = optionalUser.get();
        }

        UserState userState = user.getState();
        switch (userState) {
            case DEFAULT -> defaultHandler.handle(update.getMessage());
            case WAITING -> waitingHandler.handle(update.getMessage());
            case CHATTING -> chattingHandler.handle(update.getMessage());
            default -> {
                LOG.warn("State {} for user with 'id' = {} is not supported", userState, userId);
                user.setState(UserState.DEFAULT);
                userService.save(user);
                messageSender.sendMessage(userId,
                        NewMessageBodyBuilder.ofText("Оххх... Что-то ошибочка вышла... Попробуйте еще раз").build());
            }
        }
    }

}
