package one.coffee.keyboards;

import java.util.List;

import org.springframework.context.annotation.Profile;

import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.EndConversationButton;
import one.coffee.keyboards.buttons.ProfileButton;
import one.coffee.keyboards.buttons.StartButton;

public class DefaultHelpKeyboard extends Keyboard {
    public DefaultHelpKeyboard() {
        this.message = """
                        Список команд бота, доступных для использования:
                        /profile - редактировать профиль пользователя
                        /start - начать диалог с пользователем
                        Или воспользуйтесь кнопкой ниже
                        """;
        this.buttonsMap =
                List.of(
                        List.of(new ProfileButton(getPrefix())),
                        List.of(new StartButton(getPrefix()))
                );

    }
}
