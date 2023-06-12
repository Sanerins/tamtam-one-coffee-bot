package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.ParentClasses.Result;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.Keyboard;
import one.coffee.keyboards.WaitingKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.utils.WaitingStateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class WaitingStateHandler extends StateHandler {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private WaitingStateUtils utils;

    @Override
    public UserState getHandlingState() {
        return UserState.WAITING;
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendKeyboard(message.getSender().getUserId(), new WaitingKeyboard("""
                        Список команд бота, доступных для использования:
                        /stop - выйти из очереди
                        Либо воспользуйтесь кнопкой
                        """));
        return new StateResult(Result.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/stop")
    private StateResult handleStop(Message message) {
        return utils.handleStop(message.getSender().getUserId(), message.getSender().getUsername());
    }

    @Override
    protected Keyboard getStateBaseCommandsKeyboard() {
        return new WaitingKeyboard("""
                        Напиши мне лучше команду /help
                        """);
    }
}
