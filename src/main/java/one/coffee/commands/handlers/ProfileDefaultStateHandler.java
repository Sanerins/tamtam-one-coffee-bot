package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.DefaultProfileStateKeyboard;
import one.coffee.keyboards.FillProfileKeyboard;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.WaitingKeyboard;
import one.coffee.sql.states.UserState;
import org.springframework.stereotype.Component;

@Component
public class ProfileDefaultStateHandler extends StateHandler {
    @Override
    public UserState getHandlingState() {
        return UserState.PROFILE_DEFAULT;
    }

    @Override
    protected StateResult handleText(Message message) {
        messageSender.sendKeyboard(
                message.getSender().getUserId(),
                new FillProfileKeyboard("В начале выбери, что мы будем изменять: ")
        );
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendKeyboard(message.getSender().getUserId(), new DefaultProfileStateKeyboard("""
                        Список команд бота, доступных для использования:
                        /profile - получить форму для заполнения профиля
                        Либо воспользуйтесь кнопкой
                        """));
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @HandlerAnnotation("/profile")
    private StateResult handleProfile(Message message) {
        messageSender.sendKeyboard(message.getSender().getUserId(), new FillProfileKeyboard("Для заполнения профиля нужно выполнить каждое из действий:"));
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new DefaultProfileStateKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }
}
