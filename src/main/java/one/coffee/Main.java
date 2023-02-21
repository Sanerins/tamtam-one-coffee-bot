package one.coffee;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import one.coffee.bot.OneCoffeeBot;
import one.coffee.bot.OneCoffeeBotUpdateHandler;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        if (args.length != 1) {
            LOG.error("Wrong number of arguments on start");
            System.exit(1);
        }
        String accessToken = args[0];
        StaticContext.initialize(accessToken);
        OneCoffeeBotUpdateHandler handler = new OneCoffeeBotUpdateHandler();

        OneCoffeeBot bot = new OneCoffeeBot(StaticContext.getClient(), LongPollingBotOptions.DEFAULT, handler);
        try {
            bot.start();
        } catch (TamTamBotException e) {
            LOG.error("Failed to start bot: " + e.getMessage());
            System.exit(1);
        }
    }

}
