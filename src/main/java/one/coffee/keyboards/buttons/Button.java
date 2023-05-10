package one.coffee.keyboards.buttons;

import chat.tamtam.botapi.model.CallbackButton;
import chat.tamtam.botapi.model.Intent;

public abstract class Button {
    public final String keyboardPrefix;
    public final String text;
    protected Intent intent;
    public Button(String keyboardPrefix, String text) {
        this.keyboardPrefix = keyboardPrefix;
        this.text = text;
    }

    public String getPrefix() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return готовую кнопку, в качестве payload для {@link chat.tamtam.botapi.model.CallbackButton} мы передаем keyboardPrefix.buttonPrefix(.доп данные(это не обязательно))
     */
    public chat.tamtam.botapi.model.Button build() {
        return new CallbackButton(keyboardPrefix + "." + getPrefix(), text).intent(intent);
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
