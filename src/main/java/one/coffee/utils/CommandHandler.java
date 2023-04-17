package one.coffee.utils;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
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
                NewMessageBodyBuilder.ofText("Работяга, пришли мне команду!!! /help").build()
        );
    }

    protected void handleDefault(Message message) {
        messageSender.sendMessage(
                message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Такой команды не знаю :(").build()
        );
    }

}
