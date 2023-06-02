package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.Intent;

public class EndConversationButton extends Button {
    public EndConversationButton(String keyboardPrefix) {
        super(keyboardPrefix, "Покинуть переписку");
        intent = Intent.NEGATIVE;
    }
}
