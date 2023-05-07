package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.CallbackButton;

public class NoButton extends Button {
    public NoButton(String keyboardPrefix) {
        super(keyboardPrefix,"No");
    }

    @Override
    public CallbackButton build() {
        return new CallbackButton(keyboardPrefix + "." + getPrefix(), text);
    }
}
