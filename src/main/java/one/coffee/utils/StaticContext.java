package one.coffee.utils;

import chat.tamtam.botapi.client.TamTamClient;
import one.coffee.sql.DB;
import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UsersTable;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StaticContext {

    private static TamTamClient client;
    private static MessageSender sender;
    private static boolean isSet = false;

    public static void initialize(String token) {
        if (isSet) {
            throw new UnsupportedOperationException();
        }

        client = TamTamClient.create(token);
        sender = new MessageSender(client);
        isSet = true;
    }

    public static TamTamClient getClient() {
        return client;
    }

    public static MessageSender getMessageSender() {
        return sender;
    }

}
