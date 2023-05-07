package one.coffee.keyboards;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.builders.attachments.AttachmentsBuilder;
import chat.tamtam.bot.builders.attachments.InlineKeyboardBuilder;
import chat.tamtam.botapi.model.NewMessageBody;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Самый базовый класс, от которого наследуются все другие клавиатуры.
 * Как сделать свою клавиатуру со своими кнопками?
 * Надо создать НОВЫЙ класс, наследник этого, задать {@link #message message}, {@link #buttonsMap buttonsMap}.
 * Потом в нужном месте вызвать build.
 *
 * Как сделать хендлер?
 * Создать {@link one.coffee.callbacks.KeyboardCallbackHandler класс наследник},
 * где {@link KeyboardCallbackHandler#getKeyboardPrefix() метод} будет возвращать класс обрабатываемой клавиатуры,
 * а {@link one.coffee.keyboards.buttons.ButtonAnnotation аннотации} содержит значением класса обрабатываемой кнопки,
 * сам же метод ОБЯЗАТЕЛЬНО первым параметром должен принимать {@link chat.tamtam.botapi.model.Message}, а затем дополнительные параметры
 *
 * Таким образом кнопки можно переиспользовать в разных клавиатурах.
 *
 */
public abstract class Keyboard {
    public String getPrefix() {
        return this.getClass().getSimpleName();
    }

    /**
     * сообщение, которое будет указываться тад кнопками, оно обязательное, без него кнопки отображаться не будут
     */
    protected String message;

    /**
     * матрица кнопок
     */
    protected List<List<Button>> buttonsMap;

    protected Keyboard() {
    }

    @NotNull
    public NewMessageBody build() {
        return NewMessageBodyBuilder.ofAttachments(
                AttachmentsBuilder.inlineKeyboard(InlineKeyboardBuilder.layout(buildButtonMap(buttonsMap)))
        ).withText(message).build();
    }

    @NotNull
    private static List<List<chat.tamtam.botapi.model.Button>> buildButtonMap(List<List<Button>> buttonsMap) {
        return buttonsMap.stream().map(list -> list.stream().map(Button::build).toList()).toList();
    }
}
