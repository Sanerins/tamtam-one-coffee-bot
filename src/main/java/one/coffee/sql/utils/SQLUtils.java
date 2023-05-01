package one.coffee.sql.utils;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

import chat.tamtam.botapi.model.Message;
import one.coffee.sql.user.User;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final long DEFAULT_ID = -1;
    public static final String TABLE_SIGNATURE_START = "(";
    public static final String TABLE_SIGNATURE_END = ")";
    public static final String ARG_ATTRIBUTES_SEPARATOR = " ";
    public static final String ARGS_SEPARATOR = ", ";

    private SQLUtils() {
    }

    public static User recoverSenderIfAbsent(Message msg) {
        return StaticContext.USER_SERVICE
                .get(msg.getSender().getUserId())
                .orElseGet(() -> /*TODO NULL_USER*/ SQLUtils.recoverSender(msg).get());
    }

    // Восстановление пользователя может быть вызвано по двум причинам:
    // 1. Работяга до этого уже переписывался с ботом, разорвал соединение, мы потеряли о нём данные чудесным образом
    // (дропнули или просто почистили таблички, или же хацкер оставил нас у разбитого корыта...), а потом написал /start;
    // 2. Невалидный запрос (на любой стадии).
    public static Optional<User> recoverSender(Message msg) {
        var sender = msg.getSender();
        long senderId = sender.getUserId();
        String senderUsername = sender.getName();
        LOG.warn("No user {} in DB! Recovering his...", senderId);
        User recoveredSender = User.build()
                .setId(senderId)
                .setUsername(senderUsername)
                .get();
        return StaticContext.USER_SERVICE.save(recoveredSender);
    }

}
