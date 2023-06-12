package one.coffee.callbacks.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.keyboards.InConversationKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.StartConversationKeyboard;
import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.EndConversationButton;
import one.coffee.keyboards.buttons.SendHelloButton;
import one.coffee.sql.states.UserState;
import one.coffee.sql.user.User;
import one.coffee.utils.ChattingStateUtils;

@Component
public class StartConversationKeyboardCallback extends KeyboardCallbackHandler {

    @Autowired
    ChattingStateUtils utils;

    @Override
    public Class<? extends Keyboard> getKeyboardPrefix() {
        return StartConversationKeyboard.class;
    }

    @ButtonAnnotation(SendHelloButton.class)
    public CallbackResult ApproveButtonCallback(Message message) {
        User recipient = userConnectionService.getConnectedUser(message.getRecipient().getUserId()).get();
        User sender = userService.get(message.getRecipient().getUserId()).get();
        messageSender.sendMessage(recipient.getId(), "Привет!)");
        messageSender.sendMessage(sender.getId(), "Отправили собеседнику \"Привет!)\"");
        return new CallbackResult(Result.ResultState.SUCCESS);
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new StartConversationKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }

    @Override
    protected boolean isStateAllowed(UserState state) {
        return state == UserState.CHATTING;
    }
}
