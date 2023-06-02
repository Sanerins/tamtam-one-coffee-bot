package one.coffee.keyboards;

import java.util.List;

import one.coffee.keyboards.buttons.ApproveButton;
import one.coffee.keyboards.buttons.EndConversationButton;

public class InConversationHelpKeyboard extends Keyboard {
    public InConversationHelpKeyboard() {
        this.message = """
                        Список команд бота, доступных для использования:
                        /approve - подтвердить желание поделиться контактной информацией
                        /end - закончить диалог с пользователем
                        Или же используйте кнопки:
                        """;
        this.buttonsMap =
                List.of(
                        List.of(new ApproveButton(getPrefix())),
                        List.of(new EndConversationButton(getPrefix()))
                );

    }
}
