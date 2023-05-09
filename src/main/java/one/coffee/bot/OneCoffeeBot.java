package one.coffee.bot;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBot;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.Update;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class OneCoffeeBot extends LongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OneCoffeeBotUpdateMapper handler;

    // FIXME Доделать норм статистику
    private static long successfulCount;
    private static long unsuccessfulCount;

    @Autowired
    public OneCoffeeBot(TamTamClient client,
                        OneCoffeeBotUpdateMapper handler) {
        super(client, LongPollingBotOptions.DEFAULT);
        this.handler = handler;
    }

    @Override
    public Object onUpdate(Update update) {
        LOG.info("Received update: {}", update);
        Result result = update.map(handler);
        if (result.getResultState() == StateResult.ResultState.SUCCESS) {
            successfulCount++;
        } else {
            unsuccessfulCount--;
            LOG.error("Got error%s".formatted(result.getError()));
        }
        return result;
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
