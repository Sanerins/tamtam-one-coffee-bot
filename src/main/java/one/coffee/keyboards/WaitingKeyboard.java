package one.coffee.keyboards;

import java.util.List;

import chat.tamtam.botapi.model.Message;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;
import one.coffee.keyboards.buttons.StopButton;

public class WaitingKeyboard extends Keyboard {
    public WaitingKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new StopButton(getPrefix()))
                );

    }
}
