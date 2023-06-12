package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.Intent;

public class SendHelloButton extends Button {
    public SendHelloButton(String keyboardPrefix) {
        super(keyboardPrefix, "Поздороваться!");
        intent = Intent.POSITIVE;
    }
}
