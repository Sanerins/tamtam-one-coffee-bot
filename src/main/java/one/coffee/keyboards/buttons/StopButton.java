package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.Intent;

public class StopButton extends Button {
    public StopButton(String keyboardPrefix) {
        super(keyboardPrefix, "Выйти из ожидания");
        intent = Intent.NEGATIVE;
    }
}
