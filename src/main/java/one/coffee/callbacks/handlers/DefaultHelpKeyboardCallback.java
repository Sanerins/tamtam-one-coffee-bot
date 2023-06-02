package one.coffee.callbacks.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chat.tamtam.botapi.model.Message;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.DefaultHelpKeyboard;
import one.coffee.keyboards.InConversationHelpKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.EndConversationButton;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;
import one.coffee.utils.ChattingStateUtils;
import one.coffee.utils.DefaultStateUtils;

@Component
public class DefaultHelpKeyboardCallback extends KeyboardCallbackHandler {

    @Autowired
    DefaultStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return DefaultHelpKeyboard.class;
    }

    @ButtonAnnotation(ProfileButton.class)
    public CallbackResult ProfileButtonCallback(Message message) {
        return utils.handleProfile(message.getRecipient().getUserId(), "Person").toCallbackResult();
    }

    @ButtonAnnotation(StartButton.class)
    public CallbackResult StartButtonCallback(Message message) {
        return utils.handleStart(message.getRecipient().getUserId(), "Person").toCallbackResult();
    }
}
