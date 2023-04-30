package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.utils.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.utils.CommandHandler;
import one.coffee.utils.StaticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

//TODO: по методу handle вполне понятно что надо сделать)
public class WaitingCommandHandler extends CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final UserService userService = StaticContext.USER_SERVICE;
    private static final UserConnectionService userConnectionService = StaticContext.USER_CONNECTION_SERVICE;

    public WaitingCommandHandler() {
        super(StaticContext.getMessageSender());
    }

    @Override
    protected void handleCommand(Message message, String[] commandWithArgs) {
        long senderId = message.getSender().getUserId();
        Optional<User> optionalSender = userService.get(senderId);
        switch (commandWithArgs[0]) {
            case "/stop" -> {
                User sender;
                if (optionalSender.isEmpty()) {
                    // См. возможные причины в OneCoffeeUpdateHandler::visit(MessageCreatedUpdate)
                    sender = new User(senderId, "Cyberpunk2077", UserState.DEFAULT, message.getSender().getUsername());
                } else {
                    sender = optionalSender.get();
                }
                sender.setState(UserState.DEFAULT);
                userService.save(sender);
                messageSender.sendMessage(senderId,
                        NewMessageBodyBuilder.ofText("Ты успешно вышел из очереди!").build());
            }
        }
    }
}
