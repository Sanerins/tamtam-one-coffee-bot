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
import one.coffee.keyboards.buttons.StopButton;
import one.coffee.sql.states.UserState;
import one.coffee.utils.WaitingStateUtils;

@Component
public class WaitingHelpKeyboardCallback extends KeyboardCallbackHandler {

    @Autowired
    WaitingStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return WaitingKeyboard.class;
    }

    @ButtonAnnotation(StopButton.class)
    public CallbackResult StopButtonCallback(Message message) {
        return utils.handleStop(message.getRecipient().getUserId(), "Person").toCallbackResult();
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new WaitingKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }

    @Override
    protected boolean isStateAllowed(UserState state) {
        return state == UserState.WAITING;
    }
}
