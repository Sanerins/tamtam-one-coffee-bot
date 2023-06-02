package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.Intent;

public class ApproveButton extends Button {
    public ApproveButton(String keyboardPrefix) {
        super(keyboardPrefix, "Одобрить собеседника");
        intent = Intent.POSITIVE;
    }
}
