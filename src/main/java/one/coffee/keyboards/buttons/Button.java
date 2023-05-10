package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.CallbackButton;

public abstract class Button {
    public String getPrefix() {
        return this.getClass().getSimpleName();
    }

    public final String keyboardPrefix;
    public final String text;

    public Button(String keyboardPrefix, String text) {
        this.keyboardPrefix = keyboardPrefix;
        this.text = text;
    }

    /**
     * @return готовую кнопку, в качестве payload для {@link chat.tamtam.botapi.model.CallbackButton} мы передаем keyboardPrefix.buttonPrefix(.доп данные(это не обязательно))
     */
    public chat.tamtam.botapi.model.Button build() {
        return new CallbackButton(keyboardPrefix + "." + getPrefix(), text);
    }
}
