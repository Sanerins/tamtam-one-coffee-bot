package one.coffee;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.client.TamTamClient;
import one.coffee.bot.OneCoffeeBot;
import one.coffee.bot.OneCoffeeBotUpdateHandler;
import one.coffee.utils.MessageSender;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Wrong number of arguments");
            System.exit(1);
        }
        String accessToken = args[0];
        TamTamClient client = TamTamClient.create(accessToken);
        MessageSender sender = new MessageSender(client);
        OneCoffeeBotUpdateHandler handler = new OneCoffeeBotUpdateHandler(sender);
        OneCoffeeBot bot = new OneCoffeeBot(client,
                LongPollingBotOptions.DEFAULT,
                handler);
        try {
            bot.start();
        } catch (TamTamBotException e) {
            System.err.println("Failed to start bot: " + e.getMessage());
            System.exit(1);
        }
    }

}
