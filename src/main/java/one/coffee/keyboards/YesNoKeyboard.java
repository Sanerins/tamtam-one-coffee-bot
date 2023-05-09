package one.coffee.keyboards;

import one.coffee.keyboards.buttons.NoButton;
import one.coffee.keyboards.buttons.YesButton;

import java.util.List;

public abstract class YesNoKeyboard extends Keyboard {
    protected YesNoKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new YesButton(getPrefix()), new NoButton(getPrefix()))
                );

    }
}
