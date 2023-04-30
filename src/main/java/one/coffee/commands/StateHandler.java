package one.coffee.commands;

import chat.tamtam.bot.annotations.CommandHandler;
import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.bot.commands.CommandLineParser;
import chat.tamtam.bot.commands.RawCommandLine;
import chat.tamtam.botapi.model.Message;
import one.coffee.bot.UpdateResult;
import one.coffee.sql.UserState;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.utils.MessageSender;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.invoke.MethodHandles.filterReturnValue;

@Component
public abstract class StateHandler {
    private static MethodHandle NO_RESPONSE;

    static {
        try {
            NO_RESPONSE = MethodHandles.lookup().findStatic(StateHandler.class, "noResponse",
                    MethodType.methodType(Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // actually should never happens
            throw new RuntimeException(e);
        }
    }

    @Autowired
    protected MessageSender messageSender;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserConnectionService userConnectionService;

    protected final Map<String, CommandHandle> commandHandlers;

    protected StateHandler() {
        this.commandHandlers = new HashMap<>();
    }

    @PostConstruct
    public void postConstruct() {
        List<Class> supers = new ArrayList<>(4);
        for (Class<?> cls = this.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            supers.add(cls);
        }

        List<Method> commandHandlers = new ArrayList<>();
        for (int i = supers.size(); --i >= 0; ) {
            Class<?> cls = supers.get(i);
            for (Method m : cls.getDeclaredMethods()) {
                if (m.getAnnotation(CommandHandler.class) != null) {
                    m.setAccessible(true);
                    commandHandlers.add(m);
                }
            }
        }

        for (Method commandHandler : commandHandlers) {
            registerCommandHandler(commandHandler, this);
        }
    }

    final public UpdateResult handle(Message message) {
        if (commandHandlers.isEmpty()) {
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
        CommandHandle commandHandler = commandHandlers.get(commandKey);
        if (commandHandler == null) {
            return handleDefault(message);
        }

        String[] actualArgs;
        if (commandHandler.shouldParseArgs) {
            actualArgs = CommandLineParser.parseArgs(commandLine.getTail());
        } else {
            actualArgs = new String[]{commandLine.getTail()};
        }

        try {
            Object[] invokeArgs = new Object[commandHandler.expectedArgs + 1];
            invokeArgs[0] = message;
            System.arraycopy(actualArgs, 0, invokeArgs, 1, Math.min(actualArgs.length, commandHandler.expectedArgs));
            return (UpdateResult) commandHandler.handle.invokeWithArguments(invokeArgs);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public abstract UserState getHandlingState();


    // TODO /text не работает, на самом деле, в чате нельзя на него нажать. Надо это пофиксить.
    protected void handleText(Message message) {
        messageSender.sendMessage(
                message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Работяга, пришли мне команду!!! /help").build()
        );
    }

    protected UpdateResult handleDefault(Message message) {
        messageSender.sendMessage(
                message.getSender().getUserId(),
                NewMessageBodyBuilder.ofText("Такой команды не знаю :(").build()
        );
        return new UpdateResult(UpdateResult.UpdateState.ERROR, "Unknown command");
    }

    // Дальше лучше не смотреть - сложная логика
    private void registerCommandHandler(Method method, Object target) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length < 1) {
            throw new IllegalArgumentException(
                    "Method " + method + " must match signature: (Message message, Object... args)");
        }

        Class<?> parameterType1 = method.getParameterTypes()[0];
        if (!Message.class.isAssignableFrom(parameterType1)) {
            throw new IllegalArgumentException(
                    "Method " + method + " must have only single parameter of type `Message`");
        }

        MethodHandle commandHandler = unreflect(method);

        CommandHandler annotation = method.getAnnotation(CommandHandler.class);
        commandHandler = commandHandler.bindTo(target);

        String commandKey = prepareCommandKey(annotation.value());
        boolean shouldParseArgs = annotation.parseArgs();
        commandHandlers.put(commandKey, new CommandHandle(parameters.length - 1, shouldParseArgs, commandHandler));
    }

    private static MethodHandle unreflect(Method method) {
        MethodHandle handle;
        try {
            handle = MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException e) {
            // actually should never happens
            throw new RuntimeException(e);
        }

        if (method.getReturnType().equals(void.class)) {
            handle = filterReturnValue(handle, NO_RESPONSE);
        }

        return handle;
    }

    @NotNull
    private static String prepareCommandKey(String cmdKey) {
        if (cmdKey == null || cmdKey.isEmpty()) {
            throw new IllegalArgumentException("Command key is empty");
        }

        if (cmdKey.charAt(0) == '/') {
            return prepareCommandKey(cmdKey.substring(1));
        }

        return cmdKey.toLowerCase();
    }

    private static Object noResponse() {
        return null;
    }

    private static class CommandHandle {
        private final int expectedArgs;
        private final boolean shouldParseArgs;
        private final MethodHandle handle;

        private CommandHandle(int expectedArgs, boolean shouldParseArgs, MethodHandle handle) {
            this.expectedArgs = expectedArgs;
            this.shouldParseArgs = shouldParseArgs;
            this.handle = handle;
        }
    }
}
