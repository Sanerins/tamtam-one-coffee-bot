package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.FillProfileKeyboard;
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
        messageSender.sendMessage(
                message.getSender().getUserId(),
                "В начале выбери, что мы будем изменять."
        );
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendMessage(message.getSender().getUserId(),
                """
                        Список команд бота, доступных для использования:
                        /help - список всех команд
                        /profile - получить форму для заполнения профиля
                        """);
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @HandlerAnnotation("/profile")
    private StateResult handleProfile(Message message) {
        messageSender.sendKeyboard(message.getSender().getUserId(), new FillProfileKeyboard());
        return new StateResult(Result.ResultState.SUCCESS);
    }
}
