package one.coffee.commands;

import chat.tamtam.bot.commands.CommandLineParser;
import chat.tamtam.bot.commands.RawCommandLine;
import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Handler;
import one.coffee.ParentClasses.HandlerAnnotation;
import one.coffee.ParentClasses.Result;
import one.coffee.sql.states.UserState;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public abstract class StateHandler extends Handler {

    /**
     * @return user {@link UserState state}, which this class handles
     */
    public abstract UserState getHandlingState();

    @SuppressWarnings("unchecked")
    @Override
    protected StateResult handleDefault(Message message) {
        String text = message.getBody().getText();
        if (StringUtil.isEmpty(text) || text.charAt(0) != '/') {
            return handleText(message);
        }

        if (getStateBaseCommandsKeyboard() == null) {
            messageSender.sendMessage(
                    message.getSender().getUserId(),
                    """
                            Такой команды не знаю :(
                            Напиши лучше /help для получения списка команд
                            """
            );
        } else {
            messageSender.sendKeyboard(message.getSender().getUserId(), getStateBaseCommandsKeyboard());
        }
        return new StateResult(Result.ResultState.ERROR, "Unknown command");
    }

    protected StateResult handleText(Message message) {
        if (getStateBaseCommandsKeyboard() == null) {
            messageSender.sendMessage(
                    message.getSender().getUserId(),
                    """
                            Напиши лучше /help для получения списка команд
                            """
            );
        } else {
            messageSender.sendKeyboard(message.getSender().getUserId(), getStateBaseCommandsKeyboard());
        }
        return new StateResult(Result.ResultState.SUCCESS);
    }

    public StateResult handle(Message message) {
        if (handlers.isEmpty()) {
            return handleDefault(message);
        }

        String text = message.getBody().getText();
        if (text == null || text.isEmpty()) {
            return handleDefault(message);
        }

        RawCommandLine commandLine = CommandLineParser.parseRaw(text);
        if (commandLine == null) {
            return handleDefault(message);
        }

        String commandKey = commandLine.getKey();
        Handle handler = handlers.get(commandKey);
        if (handler == null) {
            return handleDefault(message);
        }

        String[] actualArgs = CommandLineParser.parseArgs(commandLine.getTail());

        try {
            Object[] invokeArgs = new Object[handler.expectedArgs + 1];
            invokeArgs[0] = message;
            System.arraycopy(actualArgs, 0, invokeArgs, 1, Math.min(actualArgs.length, handler.expectedArgs));
            return (StateResult) handler.handle.invokeWithArguments(invokeArgs);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    protected Class<HandlerAnnotation> getHandlerAnnotation() {
        return HandlerAnnotation.class;
    }

    @Override
    protected <A extends Annotation> String getValueFormHandlerAnnotation(A annotation) {
        return ((HandlerAnnotation) annotation).value();
    }
}
