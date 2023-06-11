package one.coffee.keyboards;

import java.util.List;

import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.EndConversationButton;
import one.coffee.keyboards.buttons.SendHelloButton;

public class StartConversationKeyboard extends Keyboard {
    public StartConversationKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new SendHelloButton(getPrefix()))
                );

    }
}
