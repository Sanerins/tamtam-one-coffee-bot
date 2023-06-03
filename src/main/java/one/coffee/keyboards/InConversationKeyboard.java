package one.coffee.keyboards;

import java.util.List;

import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.EndConversationButton;

public class InConversationKeyboard extends Keyboard {
    public InConversationKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new ApproveButton(getPrefix())),
                        List.of(new EndConversationButton(getPrefix()))
                );

    }
}
