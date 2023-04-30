package one.coffee.commands;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.sql.UserState;
import one.coffee.sql.user.User;
import one.coffee.utils.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

//TODO: по методу handle вполне понятно что надо сделать)
@Component
public class WaitingCommandHandler extends CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //FIXME ЧТО БЛЯТЬ ЗА КОНСТАНТЫ
    @Override
    protected void handleCommand(Message message, String[] commandWithArgs) {
        long senderId = message.getSender().getUserId();
        Optional<User> optionalSender = userService.get(senderId);
        switch (commandWithArgs[0]) {
            case "/stop" -> {
                User sender;
                // См. возможные причины в OneCoffeeUpdateHandler::visit(MessageCreatedUpdate)
                sender = optionalSender.orElseGet(() -> new User(senderId, "Cyberpunk2077", UserState.DEFAULT, message.getSender().getUsername()));
                sender.setState(UserState.DEFAULT);
                userService.save(sender);
                messageSender.sendMessage(senderId,
                        NewMessageBodyBuilder.ofText("Ты успешно вышел из очереди!").build());
            }
        }
    }
}
