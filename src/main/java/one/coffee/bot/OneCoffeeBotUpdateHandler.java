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
            User user = new User(userId, "Cyberpunk2077", UserState.DEFAULT);
            messageSender.sendMessage(userId,
                    NewMessageBodyBuilder.ofText("Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе \n" +
                            "Напиши /help, чтобы получить список команд!").build());
        } catch (SQLException e) {
            LOG.warn("Can't update user state in user {}", userId);
            messageSender.sendMessage(userId,
                    NewMessageBodyBuilder.ofText("Бот, призванный помочь одиноким или скучающим людям найти компанию и славно провести время вместе \n" +
                            "Напиши /help, чтобы получить список команд!").build());
        }
    }

    @Override
    public void visit(MessageCreatedUpdate update) {
        long userId = Objects.requireNonNull(update.getMessage().getSender().getUserId(), "UserId is null");

        try {
            User user = UsersTable.getUserByUserId(userId);
            if (user == null) {
                // Это возможно в двух случаях:
                // 1. Работяга до этого уже переписывался с ботом, разорвал соединение, мы потеряли о нём данные чудесным образом
                // (дропнули или просто почистили таблички, или же хацкер оставил нас у разбитого корыта...), а потом написал /start;
                // 2. Невалидный запрос (на любой стадии).
                // Действия:
                // 1. Пересоздание пользователя (реализуется);
                // 2. Описано в OneCoffeeBotUpdateHandler::visit(MessageCreatedUpdate).
                // В логи инфа уже поступит на уровне DB по поводу ошибочки.
                user = new User(userId, "Cyberpunk2077", UserState.DEFAULT);
            }
            UserState userState = user.getState();
            switch (userState) {
                case DEFAULT -> defaultHandler.handle(update.getMessage());
                case WAITING -> waitingHandler.handle(update.getMessage());
                case CHATTING -> chattingHandler.handle(update.getMessage());
                default -> {
                    LOG.warn("State {} for user with 'id' = {} is not supported", userState, userId);
                    user.setState(UserState.DEFAULT);
                    user.commit();
                    messageSender.sendMessage(userId,
                            NewMessageBodyBuilder.ofText("Оххх... Что-то ошибочка вышла... Попробуйте еще раз").build());
                }
            }
        } catch (SQLException e) {
            // Может быть две причины, почему тут упадёт исключение
            // 1. Упала база;
            // 2. Сделали невалидный запрос (и, соответственно, не смогли выпарсить результат).
            // Действия:
            // 1. Ошибка фатальная, и надо прекращать работу бота;
            // 2. Ошибка нефатальная, но дальше работать мы тоже особо не сможем, будем спамить в логи.
            // Если мы запрогали невалидный запрос ток один (например, на гет), то можем понадеяться,
            // что запрос на пут будет валидным, и тогда есть смысл продолжать дальше работу, чтобы собрать побольше инфы.
            // Однако бот будет неполноценным в этом случае и не будет полностью доступен для взаимодействия.
            // TODO Решить, что делать
            LOG.warn("Can't send message of user {}!", userId,  e);
        }
    }

}
