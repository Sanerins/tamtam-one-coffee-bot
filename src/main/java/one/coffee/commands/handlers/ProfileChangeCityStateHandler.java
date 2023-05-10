package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import org.springframework.stereotype.Component;

@Component
public class ProfileChangeCityStateHandler extends StateHandler {
    @Override
    public UserState getHandlingState() {
        return UserState.PROFILE_CHANGE_CITY;
    }

    @Override
    protected StateResult handleText(Message message) {
        User user = userService.get(message.getSender().getUserId()).get();
        String newCity = message.getBody().getText();
        user.setCity(newCity);
        messageSender.sendMessage(user.getId(), "Теперь ваш город: " + newCity);
        user.setState(UserState.PROFILE_DEFAULT);
        userService.save(user);
        return new StateResult(Result.ResultState.SUCCESS);
    }
}
