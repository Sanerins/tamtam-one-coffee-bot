package one.coffee.callbacks.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chat.tamtam.botapi.model.Message;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.InConversationHelpKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.EndConversationButton;
import one.coffee.utils.ChattingStateUtils;

@Component
public class InConversationHelpKeyboardCallback extends KeyboardCallbackHandler {

    @Autowired
    ChattingStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return InConversationHelpKeyboard.class;
    }

    @ButtonAnnotation(ApproveButton.class)
    public CallbackResult ApproveButtonCallback(Message message) {
        return utils.handleApprove(message.getRecipient().getUserId(), "Person").toCallbackResult();
    }

    @ButtonAnnotation(EndConversationButton.class)
    public CallbackResult EndConversationButtonCallback(Message message) {
        return utils.handleEnd(message.getRecipient().getUserId(), "Person").toCallbackResult();
    }
}
