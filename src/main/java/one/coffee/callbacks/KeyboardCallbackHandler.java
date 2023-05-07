package one.coffee.callbacks;

import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Handler;
import one.coffee.ParentClasses.Result;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public abstract class KeyboardCallbackHandler extends Handler {

    /**
     * @return префикс, который должен совпадать префиксом клавиатуры (чаще всего это описание действия).
     * Не путать с префиксом кнопки
     */
    public abstract Class<? extends Keyboard> getKeyboardPrefix();

    @SuppressWarnings("unchecked")
    @Override
    protected <R extends Result> R handleDefault(Message message) {
        messageSender.sendMessage(
                message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Такой кнопки не знаю :(").build()
        );
        return (R) new CallbackResult(Result.ResultState.ERROR, "Unknown button");
    }

    public CallbackResult handle(Message message, String buttonPrefix, String additionalPayload) {
        Handle handler = handlers.get(buttonPrefix);
        if (handler == null) {
            return handleDefault(message);
        }

        try {
            Object[] invokeArgs = new Object[handler.expectedArgs + 1];
            invokeArgs[0] = message;
            if (invokeArgs.length > 1 && additionalPayload != null) {
                invokeArgs[1] = additionalPayload;
            }
            return (CallbackResult) handler.handle.invokeWithArguments(invokeArgs);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    protected String prepareKey(String className) {
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("Class name is empty");
        }

        return className;
    }

    @Override
    protected Class<ButtonAnnotation> getHandlerAnnotation() {
        return ButtonAnnotation.class;
    }

    @Override
    protected <A extends Annotation> String getValueFormHandlerAnnotation(A annotation) {
        return ((ButtonAnnotation) annotation).value().getSimpleName();
    }
}
