package one.coffee.keyboards;

import java.util.List;

import one.coffee.keyboards.buttons.FinishProfileButton;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;

public class DefaultProfileHelpKeyboard extends Keyboard {
    public DefaultProfileHelpKeyboard() {
        this.message = """
                        Список команд бота, доступных для использования:
                        /profile - получить форму для заполнения профиля
                        Либо воспользуйтесь кнопкой
                        """;
        this.buttonsMap =
                List.of(
                        List.of(new ProfileButton(getPrefix())),
                        List.of(new FinishProfileButton(getPrefix()))
                );

    }
}
