package one.coffee.bot;

import java.util.Objects;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.updates.NoopUpdateVisitor;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import one.coffee.utils.MessageSender;

public class OneCoffeeBotUpdateHandler extends NoopUpdateVisitor {

    private final MessageSender messageSender;

    public OneCoffeeBotUpdateHandler(MessageSender messageSender) {
        super();
        this.messageSender = messageSender;
    }

    @Override
    public void visit(MessageCreatedUpdate model) {
        Long userId = Objects.requireNonNull(model.getMessage().getRecipient().getUserId(), "chatId");
        messageSender.sendMessage(userId, NewMessageBodyBuilder.ofText("Hello!").build());
    }

}
