package one.coffee.callbacks.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.DefaultProfileHelpKeyboard;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;
import one.coffee.utils.DefaultProfileStateUtils;

@Component
public class DefaultProfileHelpKeyboardCallback extends KeyboardCallbackHandler {

    @Autowired
    DefaultProfileStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return DefaultProfileHelpKeyboard.class;
    }

    @ButtonAnnotation(ProfileButton.class)
    public CallbackResult ProfileButtonCallback(Message message) {
        messageSender.sendKeyboard(message.getRecipient().getUserId(), new FillProfileKeyboard());
        return new CallbackResult(Result.ResultState.SUCCESS);
    }

    @ButtonAnnotation(StartButton.class)
    public CallbackResult FinishProfileButtonCallback(Message message) {
        return utils.finishProfile(message.getRecipient().getUserId()).toCallbackResult();
    }
}
