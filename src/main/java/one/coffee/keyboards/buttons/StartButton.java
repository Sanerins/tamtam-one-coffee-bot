package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.Intent;

public class StartButton extends Button {
    public StartButton(String keyboardPrefix) {
        super(keyboardPrefix, "Начать диалог");
        intent = Intent.POSITIVE;
    }
}
