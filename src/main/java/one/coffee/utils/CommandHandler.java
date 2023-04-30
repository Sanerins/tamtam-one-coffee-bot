package one.coffee.utils;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionService;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class CommandHandler {

    @Autowired
    protected MessageSender messageSender;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserConnectionService userConnectionService;

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
