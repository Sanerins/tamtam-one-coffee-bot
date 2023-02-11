package one.coffee.bot;

import org.jetbrains.annotations.Nullable;

import chat.tamtam.bot.longpolling.LongPollingBot;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.Update;

public class OneCoffeeBot extends LongPollingBot {

    private final OneCoffeeBotUpdateHandler handler;

    public OneCoffeeBot(TamTamClient client, LongPollingBotOptions options, OneCoffeeBotUpdateHandler handler) {
        super(client, options);
        this.handler = handler;
    }

    @Nullable
    @Override
    public Object onUpdate(Update update) {
        update.visit(handler);
        return null;
    }
}
