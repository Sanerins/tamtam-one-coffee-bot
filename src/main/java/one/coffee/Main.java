package one.coffee;

import java.lang.invoke.MethodHandles;

import one.coffee.sql.entities.User;
import one.coffee.sql.entities.UserConnection;
import one.coffee.sql.entities.UserState;
import one.coffee.sql.tables.UsersTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        testSQL();
    }

    public static void testSQL() {
        User user1 = new User(2077, "St. Petersburg", UserState.DEFAULT, null);
        User user2 = new User(2078, "Moscow", UserState.DEFAULT, null);
        UserConnection userConnection = new UserConnection(user1, user2);

        System.out.println(UsersTable.getUserById(2077));
        System.out.println(UsersTable.getUserById(2078));
    }

}
