package one.coffee.keyboards;

import java.util.List;

import one.coffee.keyboards.buttons.FinishProfileButton;
import one.coffee.keyboards.buttons.ProfileButton;

public class InitialProfileStateKeyboard extends Keyboard {
    public InitialProfileStateKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new ProfileButton(getPrefix(), "Создать профиль"))
                );

    }
}
