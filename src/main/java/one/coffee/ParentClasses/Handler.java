package one.coffee.ParentClasses;

import chat.tamtam.botapi.model.Message;
import one.coffee.sql.user.UserService;
import one.coffee.sql.user_connection.UserConnectionService;
import one.coffee.utils.MessageSender;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
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
public abstract class Handler
        //<A extends Annotation, CA extends Class<A>>
{
    private static final MethodHandle NO_RESPONSE;

    static {
        try {
            NO_RESPONSE = MethodHandles.lookup().findStatic(Handler.class, "noResponse",
                    MethodType.methodType(Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // actually should never happens
            throw new RuntimeException(e);
        }
    }

    protected final Map<String, Handle> handlers;
    @Autowired
    protected MessageSender messageSender;
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserConnectionService userConnectionService;

    protected Handler() {
        handlers = new HashMap<>();
    }

    // Дальше лучше не смотреть - сложная логика
    protected static MethodHandle unreflect(Method method) {
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

    private static Object noResponse() {
        return null;
    }

    @PostConstruct
    private void postConstruct() {
        List<Class<?>> supers = new ArrayList<>(4);
        for (Class<?> cls = this.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            supers.add(cls);
        }

        List<Method> handlers = new ArrayList<>();
        for (int i = supers.size(); --i >= 0; ) {
            Class<?> cls = supers.get(i);
            for (Method m : cls.getDeclaredMethods()) {
                if (m.getAnnotation(getHandlerAnnotation()) != null) {
                    m.setAccessible(true);
                    handlers.add(m);
                }
            }
        }

        for (Method handler : handlers) {
            registerHandler(handler, this);
        }
    }

    protected abstract Class<? extends Annotation> getHandlerAnnotation();

    protected abstract <A extends Annotation> String getValueFormHandlerAnnotation(A annotation);

    protected abstract <R extends Result> R handleDefault(Message message);

    protected void registerHandler(Method method, Object target) {
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

        Class<?> returnType = method.getReturnType();
        if (!Result.class.isAssignableFrom(returnType)) {
            throw new IllegalArgumentException(
                    "Method" + method + " must return `Result` class or predecessor"
            );
        }

        MethodHandle handler = unreflect(method);
        handler = handler.bindTo(target);

        String key = prepareKey(getValueFormHandlerAnnotation(method.getAnnotation(getHandlerAnnotation())));
        handlers.put(key, new Handle(parameters.length - 1, handler));
    }

    @NotNull
    protected String prepareKey(String cmdKey) {
        if (cmdKey == null || cmdKey.isEmpty()) {
            throw new IllegalArgumentException("Command key is empty");
        }

        if (cmdKey.charAt(0) == '/') {
            return prepareKey(cmdKey.substring(1));
        }

        return cmdKey.toLowerCase();
    }

    public static class Handle {
        public final int expectedArgs;
        public final MethodHandle handle;

        public Handle(int expectedArgs, MethodHandle handle) {
            this.expectedArgs = expectedArgs;
            this.handle = handle;
        }
    }
}
