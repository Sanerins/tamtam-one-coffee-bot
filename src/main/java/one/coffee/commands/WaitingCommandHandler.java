package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;

//TODO: по методу handle вполне понятно что надо сделать)
public class WaitingCommandHandler extends CommandHandler {

    public WaitingCommandHandler() {
        super(StaticContext.getMessageSender());
    }

    @Override
    public void handle(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Сбежать из очереди пока что нельзя))))) Так что жди начала, работяга...").build());
    }
}
