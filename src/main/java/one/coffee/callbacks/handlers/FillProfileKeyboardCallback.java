package one.coffee.callbacks.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.ChangeCityButton;
import one.coffee.keyboards.buttons.ChangeDescriptionButton;
import one.coffee.keyboards.buttons.ChangeNameButton;
import one.coffee.keyboards.buttons.FinishProfileButton;
import one.coffee.keyboards.buttons.ProfileInfoButton;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.sql.user.UserService;
import one.coffee.utils.DefaultProfileStateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FillProfileKeyboardCallback extends KeyboardCallbackHandler {
    @Autowired
    private DefaultProfileStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return FillProfileKeyboard.class;
    }

    @ButtonAnnotation(ProfileInfoButton.class)
    public CallbackResult ProfileInfoButtonCallback(Message message) {
        User user = userService.get(message.getRecipient().getUserId()).get();
        String name = UserService.checkString(user.getUsername()) ? user.getUsername() : "отсутствует";
        String city = UserService.checkString(user.getCity()) ? user.getCity() : "отсутствует";
        String credits = UserService.checkString(user.getUserInfo()) ? user.getUserInfo() : "отсутствуют";
        String result = "Информация из профиля: \n" +
                "Имя: " + name + '\n' +
                "Город: " + city + '\n' +
                "Контакты: " + credits;
        messageSender.sendKeyboard(message.getRecipient().getUserId(),
                new FillProfileKeyboard(result));
        return new CallbackResult(Result.ResultState.SUCCESS);
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
        messageSender.sendMessage(user.getId(), "Введите контакты, которые вы хотите сообщить юзеру при обоюдном согласии продолжить диалог");
        return new CallbackResult(Result.ResultState.SUCCESS);
    }

    @ButtonAnnotation(FinishProfileButton.class)
    public CallbackResult FinishProfileButtonCallback(Message message) {
        return utils.finishProfile(message.getRecipient().getUserId()).toCallbackResult();
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new FillProfileKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }

    @Override
    protected boolean isStateAllowed(UserState state) {
        return state == UserState.PROFILE_DEFAULT;
    }
}
