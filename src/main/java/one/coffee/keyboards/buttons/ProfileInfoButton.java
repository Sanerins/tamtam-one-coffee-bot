package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.Intent;

public class ProfileInfoButton extends Button {
    public ProfileInfoButton(String keyboardPrefix) {
        super(keyboardPrefix, "Показать профиль");
        intent = Intent.POSITIVE;
    }
}
