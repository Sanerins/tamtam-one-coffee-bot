package one.coffee.callbacks.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.DefaultProfileStateKeyboard;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.FinishProfileButton;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;
import one.coffee.sql.states.UserState;
import one.coffee.utils.DefaultProfileStateUtils;

@Component
public class DefaultProfileHelpKeyboardCallback extends KeyboardCallbackHandler {

    @Autowired
    DefaultProfileStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return DefaultProfileStateKeyboard.class;
    }

    @ButtonAnnotation(ProfileButton.class)
    public CallbackResult ProfileButtonCallback(Message message) {
        messageSender.sendKeyboard(message.getRecipient().getUserId(), new FillProfileKeyboard("Для заполнения профиля нужно выполнить каждое из действий:"));
        return new CallbackResult(Result.ResultState.SUCCESS);
    }

    @ButtonAnnotation(FinishProfileButton.class)
    public CallbackResult FinishProfileButtonCallback(Message message) {
        return utils.finishProfile(message.getRecipient().getUserId()).toCallbackResult();
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new DefaultProfileStateKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }

    @Override
    protected boolean isStateAllowed(UserState state) {
        return state == UserState.PROFILE_DEFAULT;
    }
}
