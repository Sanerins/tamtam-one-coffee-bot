package one.coffee.callbacks.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.ChangeCityButton;
import one.coffee.keyboards.buttons.ChangeDescriptionButton;
import one.coffee.keyboards.buttons.ChangeNameButton;
import one.coffee.keyboards.buttons.FinishProfileButton;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import org.springframework.stereotype.Component;

@Component
public class FillProfileKeyboardCallback extends KeyboardCallbackHandler {
    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return FillProfileKeyboard.class;
    }

    @ButtonAnnotation(ChangeNameButton.class)
    public CallbackResult ChangeNameButtonCallback(Message message) {
        User user = userService.get(message.getRecipient().getUserId()).get();
        user.setState(UserState.PROFILE_CHANGE_NAME);
        userService.save(user);
        messageSender.sendMessage(user.getId(), "Введите ваше имя");
        return new CallbackResult(Result.ResultState.SUCCESS);
    }

    @ButtonAnnotation(ChangeCityButton.class)
    public CallbackResult ChangeCityButtonCallback(Message message) {
        User user = userService.get(message.getRecipient().getUserId()).get();
        user.setState(UserState.PROFILE_CHANGE_CITY);
        userService.save(user);
        messageSender.sendMessage(user.getId(), "Введите ваш новый город");
        return new CallbackResult(Result.ResultState.SUCCESS);
    }


    @ButtonAnnotation(ChangeDescriptionButton.class)
    public CallbackResult ChangeDescriptionButtonCallback(Message message) {
        User user = userService.get(message.getRecipient().getUserId()).get();
        user.setState(UserState.PROFILE_CHANGE_DESCRIPTION);
        userService.save(user);
        messageSender.sendMessage(user.getId(), "Введите ваше новое описание");
        return new CallbackResult(Result.ResultState.SUCCESS);
    }

    @ButtonAnnotation(FinishProfileButton.class)
    public CallbackResult FinishProfileButtonCallback(Message message) {
        User user = userService.get(message.getRecipient().getUserId()).get();
        if (!user.getUsername().isBlank()
                && !user.getCity().isBlank()
                && !user.getUserInfo().isBlank()) {
            user.setState(UserState.DEFAULT);
            userService.save(user);
            messageSender.sendMessage(user.getId(), "Вы успешно создали профиль. Используйте /help, чтобы посмотреть новые возможности");
        } else {
            messageSender.sendMessage(user.getId(), "Вы еще не полностью заполнили профиль");
        }
        return new CallbackResult(Result.ResultState.SUCCESS);
    }
}
