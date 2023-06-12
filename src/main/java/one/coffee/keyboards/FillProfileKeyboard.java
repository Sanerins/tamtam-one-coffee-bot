package one.coffee.keyboards;

import one.coffee.keyboards.buttons.ChangeCityButton;
import one.coffee.keyboards.buttons.ChangeDescriptionButton;
import one.coffee.keyboards.buttons.ChangeNameButton;
import one.coffee.keyboards.buttons.FinishProfileButton;
import one.coffee.keyboards.buttons.ProfileInfoButton;

import java.util.List;

public class FillProfileKeyboard extends Keyboard {
    public FillProfileKeyboard(String message) {
        this.message = message;
        this.buttonsMap =
                List.of(
                        List.of(new ChangeNameButton(getPrefix())),
                        List.of(new ChangeCityButton(getPrefix())),
                        List.of(new ChangeDescriptionButton(getPrefix())),
                        List.of(new ProfileInfoButton(getPrefix())),
                        List.of(new FinishProfileButton(getPrefix()))
                );

    }
}
