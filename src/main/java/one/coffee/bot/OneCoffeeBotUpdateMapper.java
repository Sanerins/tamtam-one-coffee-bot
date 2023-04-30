package one.coffee.bot;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.updates.DefaultUpdateMapper;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import chat.tamtam.botapi.model.Update;
import one.coffee.commands.ChattingCommandHandler;
import one.coffee.commands.DefaultCommandHandler;
import one.coffee.commands.WaitingCommandHandler;
import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.utils.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

@Component
public class OneCoffeeBotUpdateMapper extends DefaultUpdateMapper<UpdateResult> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private UserService userService;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private DefaultCommandHandler defaultHandler;
    @Autowired
    private ChattingCommandHandler chattingHandler;
    @Autowired
    private WaitingCommandHandler waitingHandler;

    @Override
    public UpdateResult map(BotStartedUpdate model) {
        long userId = Objects.requireNonNull(model.getUser().getUserId(), "UserId is null");
        User user = new User(userId, "Cyberpunk2077", UserState.DEFAULT, model.getUser().getUsername());
        userService.save(user);
        messageSender.sendMessage(userId,
                NewMessageBodyBuilder.ofText("Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе \n" +
                        "Напиши /help, чтобы получить список команд!").build());
        return new UpdateResult(UpdateResult.UpdateState.SUCCESS);
    }

    @Override
    public UpdateResult map(MessageCreatedUpdate update) {
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
        return new UpdateResult(UpdateResult.UpdateState.SUCCESS);
    }

    @Override
    public UpdateResult mapDefault(Update model) {
        return new UpdateResult(UpdateResult.UpdateState.ERROR, "Unknown update type");
    }
}
