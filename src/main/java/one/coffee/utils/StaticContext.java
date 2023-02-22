package one.coffee.utils;

import chat.tamtam.botapi.client.TamTamClient;
import one.coffee.sql.entities.User;
import one.coffee.sql.tables.UserConnectionsTable;
import one.coffee.sql.tables.UsersTable;

public class StaticContext {

    public static final UsersTable USERS_TABLE = UsersTable.getInstance();
    public static final UserConnectionsTable USER_CONNECTIONS_TABLE = UserConnectionsTable.getInstance();

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
