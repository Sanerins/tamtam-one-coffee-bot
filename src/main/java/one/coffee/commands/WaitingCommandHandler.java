package one.coffee.commands;

import chat.tamtam.botapi.model.Message;
import one.coffee.sql.utils.SQLUtils;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;

public class WaitingCommandHandler extends CommandHandler {

    public WaitingCommandHandler() {
        super(StaticContext.getMessageSender());
    }

    @Override
    protected void handleCommand(Message message, String[] commandWithArgs) {
        long senderId = message.getSender().getUserId();
        switch (commandWithArgs[0]) {
            case "/stop" -> {
                SQLUtils.recoverSenderIfAbsent(message);
                messageSender.sendMessage(
                        senderId,
                        "Ты успешно вышел из очереди!"
                );
            }
            default -> {
                messageSender.sendMessage(
                        senderId,
                        "Не знаю такой комнады!!!"
                );
            }
        }
    }
}
