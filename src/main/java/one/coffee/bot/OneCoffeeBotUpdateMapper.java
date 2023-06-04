package one.coffee.bot;

import chat.tamtam.bot.updates.DefaultUpdateMapper;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.MessageCallbackUpdate;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import chat.tamtam.botapi.model.Update;
import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.DefaultProfileStateKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.utils.SQLUtils;
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
public class OneCoffeeBotUpdateMapper extends DefaultUpdateMapper<Result> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserService userService;
    @Autowired
    private MessageSender messageSender;

    private final Map<UserState, StateHandler> handlersMap;
    private final Map<String, KeyboardCallbackHandler> callbacksMap;

    @Autowired
    public OneCoffeeBotUpdateMapper(List<StateHandler> handlers, List<KeyboardCallbackHandler> callbacks) {
        // Получаем мапу, где ключ - это состояние, а значение стейтХендлер для конкретного состояния, содержащий внутри свою мапу с хендлерами для конктреной команды
        this.handlersMap = handlers.stream().collect(Collectors.toMap(StateHandler::getHandlingState, Function.identity()));
        // Получаем мапу, где ключ - id каллбека, а значение - хендлеры. Мы мерджим все хендеры
        this.callbacksMap = callbacks.stream().collect(Collectors.toMap(handler -> handler.getKeyboardPrefix().getSimpleName(), Function.identity()));
    }

    @Override
    public StateResult map(BotStartedUpdate model) {
        long userId = Objects.requireNonNull(model.getUser().getUserId(), "UserId is null");
        User user = new User(userId, "Cyberpunk2077", UserState.PROFILE_DEFAULT, SQLUtils.DEFAULT_ID, model.getUser().getUsername(), null);
        userService.save(user);
        messageSender.sendKeyboard(userId, new DefaultProfileStateKeyboard("Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе " +
                "жми на кнопку чтобы перейти к заполнению профиля!"));
        return new StateResult(StateResult.ResultState.SUCCESS);
    }

    @Override
    public StateResult map(MessageCreatedUpdate update) {
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
    public CallbackResult map(MessageCallbackUpdate model) {
        String payload = model.getCallback().getPayload();
        // first part is keyboard prefix, second is button prefix
        String[] split = payload.split("\\.", 3);
        String keyboardPrefix = split[0];
        String buttonPrefix = split[1];
        String additionalPayload = (split.length==3) ? split[2] : null;
        KeyboardCallbackHandler keyboardCallbackHandler = callbacksMap.get(keyboardPrefix);
        return keyboardCallbackHandler.handle(model.getMessage(), buttonPrefix, additionalPayload);
    }

    @Override
    public StateResult mapDefault(Update model) {
        return new StateResult(StateResult.ResultState.ERROR, "Unknown update type");
    }
}
