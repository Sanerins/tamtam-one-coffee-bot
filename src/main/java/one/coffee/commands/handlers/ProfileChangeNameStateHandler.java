package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import org.springframework.stereotype.Component;

@Component
public class ProfileChangeNameStateHandler extends StateHandler {
    @Override
    public UserState getHandlingState() {
        return UserState.PROFILE_CHANGE_NAME;
    }

    @Override
    protected StateResult handleText(Message message) {
        User user = userService.get(message.getSender().getUserId()).get();
        String newName = message.getBody().getText();
        user.setUsername(newName);
        messageSender.sendKeyboard(user.getId(), new FillProfileKeyboard("Теперь ваше имя: " + newName));
        user.setState(UserState.PROFILE_DEFAULT);
        userService.save(user);
        return new StateResult(Result.ResultState.SUCCESS);
    }
}
