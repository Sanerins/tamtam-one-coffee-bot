package one.coffee.bot;

import java.lang.invoke.MethodHandles;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBot;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.Update;

public class OneCoffeeBot extends LongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OneCoffeeBotUpdateHandler handler;

    public OneCoffeeBot(TamTamClient client,
                        LongPollingBotOptions options,
                        OneCoffeeBotUpdateHandler handler) {
        super(client, options);
        this.handler = handler;
    }

    @Nullable
    @Override
    public Object onUpdate(Update update) {
        LOG.info("Received update: {}", update);
        update.visit(handler);
        return null;
    }

    @Override
    public void start() throws TamTamBotException {
        super.start();
        LOG.info("Bot started");
    }

    @Override
    public void stop() {
        super.stop();
        LOG.info("Bot stopped");
    }
}
