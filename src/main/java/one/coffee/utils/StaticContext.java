package one.coffee.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.client.TamTamClient;
import one.coffee.bot.OneCoffeeBot;
import one.coffee.bot.OneCoffeeBotUpdateHandler;
import one.coffee.sql.user.UserDao;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionDao;
import one.coffee.sql.user_connection.UserConnectionService;

public class StaticContext {

    public static final String STRING_QUOTTER = "'";
    public static final UserDao USER_DAO = UserDao.getInstance();
    public static final UserService USER_SERVICE = UserService.getInstance();
    public static final UserConnectionDao USER_CONNECTION_DAO = UserConnectionDao.getInstance();
    public static final UserConnectionService USER_CONNECTION_SERVICE = UserConnectionService.getInstance();

    private static TamTamClient client;
    private static MessageSender sender;
    private static OneCoffeeBot bot;
    private static final AtomicBoolean isSet = new AtomicBoolean();
    private static final AtomicBoolean isRecreatingTablesNeeded = new AtomicBoolean();

    public static void initialize(String token) {
        if (isSet.get()) {
            throw new UnsupportedOperationException();
        }

        client = TamTamClient.create(token);
        sender = new MessageSender(client);
        bot = new OneCoffeeBot(client, LongPollingBotOptions.DEFAULT, new OneCoffeeBotUpdateHandler());
        isSet.set(true);
        isRecreatingTablesNeeded.set(true);
    }

    public static TamTamClient getClient() {
        return client;
    }

    public static MessageSender getMessageSender() {
        return sender;
    }

    public static OneCoffeeBot getBot() {
        return bot;
    }

    public static AtomicBoolean getIsRecreatingTablesNeeded() {
        return isRecreatingTablesNeeded;
    }

    public static void setIsRecreatingTablesNeeded(boolean v) {
        isRecreatingTablesNeeded.set(v);
    }

}
