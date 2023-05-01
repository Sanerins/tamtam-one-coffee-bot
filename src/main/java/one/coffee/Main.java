package one.coffee;

import java.lang.invoke.MethodHandles;

import chat.tamtam.bot.exceptions.TamTamBotException;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        if (args.length < 1) {
            LOG.error("Wrong number of arguments ({}, {}) on start", args, args.length);
            System.exit(1);
        }
        String accessToken = args[0];
        StaticContext.initialize(accessToken);
        if (args.length == 2) {
            boolean isRecreatingTablesNeeded = Boolean.parseBoolean(args[1]);
            StaticContext.setIsRecreatingTablesNeeded(isRecreatingTablesNeeded);
        }

        try {
            StaticContext.getBot().start();
        } catch (TamTamBotException e) {
            LOG.error("Failed to start bot: " + e.getMessage());
            System.exit(1);
        }
    }

}
