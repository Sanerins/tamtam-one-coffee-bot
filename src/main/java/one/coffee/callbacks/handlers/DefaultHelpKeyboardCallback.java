package one.coffee.callbacks.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chat.tamtam.botapi.model.Message;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.WaitingKeyboard;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;
import one.coffee.utils.DefaultStateUtils;

@Component
public class DefaultHelpKeyboardCallback extends KeyboardCallbackHandler {

    @Autowired
    DefaultStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return DefaultStateKeyboard.class;
    }

    @ButtonAnnotation(ProfileButton.class)
    public CallbackResult ProfileButtonCallback(Message message) {
        return utils.handleProfile(message.getRecipient().getUserId(), "Person").toCallbackResult();
    }

    @ButtonAnnotation(StartButton.class)
    public CallbackResult StartButtonCallback(Message message) {
        return utils.handleStart(message.getRecipient().getUserId(), "Person").toCallbackResult();
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new DefaultStateKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }
}
