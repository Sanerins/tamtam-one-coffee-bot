package one.coffee.utils;

import chat.tamtam.botapi.client.TamTamClient;

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
