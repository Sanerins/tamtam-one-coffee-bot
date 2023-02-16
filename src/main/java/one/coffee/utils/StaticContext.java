package one.coffee.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import chat.tamtam.botapi.client.TamTamClient;
import one.coffee.sql.DB;

public class StaticContext {

    private static TamTamClient client;
    private static MessageSender sender;
    //TODO: userStateMap, userWaitList, userConnections надо будет сделать при помощи SQL бд. Сейчас мы теряем все стейты, диалоги и очередь при перезагрузке
    //TODO: userWaitList должен учитывать из какого города юзер
    //TODO: userConnections сейчас - полный кринж, ибо храним две инвентированные пары ключ-значение, чтобы собеседники общались
    //TODO: сделал так, ибо сделаем нормально в SQL бд, будет табличка connection с двумя лонгами и id.
    private static ConcurrentMap<Long, UserState> userStateMap;
    private static ConcurrentMap<Long, Long> userConnections;
    private static BlockingQueue<Long> userWaitList;
    private static final DB db = new DB();

    private static boolean isSet = false;

    public static void initialize(String token) {
        if (isSet) {
            throw new UnsupportedOperationException();
        }
        client = TamTamClient.create(token);
        sender = new MessageSender(client);
        userStateMap = loadUserStateMap();
        userConnections = loadUserConnections();
        userWaitList = loadUserWaitList();

        isSet = true;
    }

    public static TamTamClient getClient() {
        return client;
    }

    public static MessageSender getMessageSender() {
        return sender;
    }

    public static ConcurrentMap<Long, UserState> getUserStateMap() {
        return userStateMap;
    }

    public static ConcurrentMap<Long, Long> getConnections() {
        return userConnections;
    }

    public static BlockingQueue<Long> getUserWaitList() {
        return userWaitList;
    }

    private static ConcurrentMap<Long, UserState> loadUserStateMap() {
        return db.getUserStates();
    }

    private static ConcurrentMap<Long, Long> loadUserConnections() {
        return new ConcurrentHashMap<>();
    }

    private static BlockingQueue<Long> loadUserWaitList() {
        return new LinkedBlockingQueue<>();
    }
}
