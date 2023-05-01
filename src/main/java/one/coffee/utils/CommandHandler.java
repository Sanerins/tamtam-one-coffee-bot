package one.coffee.utils;

import chat.tamtam.botapi.model.Message;
import org.eclipse.jetty.util.StringUtil;

public abstract class CommandHandler {

    protected final MessageSender messageSender;

    protected CommandHandler(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void handle(Message message) {
        String text = message.getBody().getText();
        if (StringUtil.isEmpty(text) || text.charAt(0) != '/') {
            handleText(message);
            return;
        }

        handleCommand(message, text.split(" "));
    }

    protected void handleCommand(Message message, String[] commandWithArgs) {
        handleDefault(message);
    }


    // TODO /text не работает, на самом деле, в чате нельзя на него нажать. Надо это пофиксить.
    protected void handleText(Message message) {
        messageSender.sendMessage(
                message.getSender().getUserId(),
                "Работяга, пришли мне команду!!! /help"
        );
    }

    protected void handleDefault(Message message) {
        messageSender.sendMessage(
                message.getSender().getUserId(),
                "Такой команды не знаю :("
        );
    }

}
