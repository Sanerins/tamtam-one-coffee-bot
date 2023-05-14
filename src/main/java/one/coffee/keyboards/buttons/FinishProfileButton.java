package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.Intent;

public class FinishProfileButton extends Button {
    public FinishProfileButton(String keyboardPrefix) {
        super(keyboardPrefix, "Закончить изменение профиля");
        intent = Intent.POSITIVE;
    }
}
