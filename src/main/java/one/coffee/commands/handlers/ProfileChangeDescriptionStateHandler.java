package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import org.springframework.stereotype.Component;

@Component
public class ProfileChangeDescriptionStateHandler extends StateHandler {
    @Override
    public UserState getHandlingState() {
        return UserState.PROFILE_CHANGE_DESCRIPTION;
    }

    @Override
    protected StateResult handleText(Message message) {
        User user = userService.get(message.getSender().getUserId()).get();
        String newDesc = message.getBody().getText();
        user.setUserInfo(newDesc);
        messageSender.sendKeyboard(user.getId(), new FillProfileKeyboard("Теперь ваши контакты: " + newDesc));
        user.setState(UserState.PROFILE_DEFAULT);
        userService.save(user);
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(), "Ввведи свои новые контакты!");
        return new StateResult(StateResult.ResultState.SUCCESS);
    }
}
