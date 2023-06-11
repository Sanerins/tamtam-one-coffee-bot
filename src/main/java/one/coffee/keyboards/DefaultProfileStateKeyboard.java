package one.coffee.keyboards;

import java.util.List;

import one.coffee.keyboards.buttons.FinishProfileButton;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;

public class DefaultProfileStateKeyboard extends Keyboard {
    public DefaultProfileStateKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new ProfileButton(getPrefix(), "Изменить профиль")),
                        List.of(new FinishProfileButton(getPrefix()))
                );

    }
}
