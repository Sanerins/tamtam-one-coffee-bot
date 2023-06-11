package one.coffee.keyboards;

import java.util.List;

import org.springframework.context.annotation.Profile;

import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.EndConversationButton;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;

public class DefaultStateKeyboard extends Keyboard {
    public DefaultStateKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new ProfileButton(getPrefix(), "Редактировать профиль")),
                        List.of(new StartButton(getPrefix()))
                );

    }
}
