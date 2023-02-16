package one.coffee;

import java.lang.invoke.MethodHandles;

import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UsersTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import one.coffee.bot.OneCoffeeBot;
import one.coffee.bot.OneCoffeeBotUpdateHandler;
import one.coffee.utils.StaticContext;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
//        if (args.length != 1) {
//            LOG.error("Wrong number of arguments on start");
//            System.exit(1);
//        }
//        String accessToken = args[0];
//        StaticContext.initialize(accessToken);
//        OneCoffeeBotUpdateHandler handler = new OneCoffeeBotUpdateHandler();
//
//        OneCoffeeBot bot = new OneCoffeeBot(StaticContext.getClient(), LongPollingBotOptions.DEFAULT, handler);
//        try {
//            bot.start();
//        } catch (TamTamBotException e) {
//            LOG.error("Failed to start bot: " + e.getMessage());
//            System.exit(1);
//        }
        UsersTable.putUser(new User(2077, "St. Petersburg",
                new UserState(1, "Pretty good state!"), new UserConnection(333, 1, 2)));
        System.out.println(UsersTable.getUserById(2077));
    }

}
