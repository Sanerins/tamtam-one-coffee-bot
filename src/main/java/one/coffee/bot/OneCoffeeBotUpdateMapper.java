package one.coffee.bot;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.updates.DefaultUpdateMapper;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import chat.tamtam.botapi.model.Update;
import one.coffee.commands.StateHandler;
import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.utils.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OneCoffeeBotUpdateMapper extends DefaultUpdateMapper<UpdateResult> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private UserService userService;
    @Autowired
    private MessageSender messageSender;

    private final Map<UserState, StateHandler> handlersMap;

    @Autowired
    public OneCoffeeBotUpdateMapper(List<StateHandler> handlers) {
        handlersMap = handlers.stream().collect(Collectors.toMap(StateHandler::getHandlingState, Function.identity()));
    }

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
        return handlersMap.get(userState).handle(update.getMessage());
    }

    @Override
    public UpdateResult mapDefault(Update model) {
        return new UpdateResult(UpdateResult.UpdateState.ERROR, "Unknown update type");
    }
}
