package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.CallbackButton;

public class YesButton extends Button {
    public YesButton(String keyboardPrefix) {
        super(keyboardPrefix,"Yes");
    }

    @Override
    public CallbackButton build() {
        return new CallbackButton(keyboardPrefix + "." + getPrefix(), text);
    }
}
