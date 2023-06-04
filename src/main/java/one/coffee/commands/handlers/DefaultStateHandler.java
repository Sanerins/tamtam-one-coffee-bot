package one.coffee.commands.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.commands.StateHandler;
import one.coffee.commands.StateResult;
import one.coffee.keyboards.DefaultStateKeyboard;
import one.coffee.sql.states.UserState;
import one.coffee.utils.DefaultStateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class DefaultStateHandler extends StateHandler {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private DefaultStateUtils utils;

    @Override
    public UserState getHandlingState() {
        return UserState.DEFAULT;
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/help")
    private StateResult handleHelp(Message message) {
        messageSender.sendKeyboard(message.getSender().getUserId(), new DefaultStateKeyboard("""
                        Список команд бота, доступных для использования:
                        /profile - редактировать профиль пользователя
                        /start - начать диалог с пользователем
                        Или воспользуйтесь кнопкой ниже
                        """));
        return new StateResult(StateResult.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @HandlerAnnotation("/start")
    private StateResult handleStart(Message message) {
        return utils.handleStart(message.getSender().getUserId(), message.getSender().getUsername());
    }

    @HandlerAnnotation("/profile")
    private StateResult handleProfile(Message message) {
        return utils.handleProfile(message.getSender().getUserId(), message.getSender().getUsername());
    }
}
