package chat.tamtam.bot;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import chat.tamtam.bot.annotations.CommandHandler;
import chat.tamtam.bot.annotations.UpdateHandler;
import chat.tamtam.bot.chat.CommandLineParser;
import chat.tamtam.bot.chat.RawCommandLine;
import chat.tamtam.bot.updates.DefaultUpdateMapper;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.Message;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import chat.tamtam.botapi.model.Update;

import static java.lang.invoke.MethodHandles.filterReturnValue;

/**
 * @author alexandrchuprin
 */
public class TamTamBotBase implements TamTamBot {
    private static MethodHandle NO_RESPONSE;

    static {
        try {
            NO_RESPONSE = MethodHandles.lookup().findStatic(TamTamBotBase.class, "noResponse",
                    MethodType.methodType(Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // actually should never happens
            throw new RuntimeException(e);
        }
    }

    private final TamTamClient client;
    private final Map<Class<? extends Update>, MethodHandle> updateHandlers;
    private final Map<String, CommandHandle> commandHandlers;

    private final Update.Mapper<Object> rootHandler = new DefaultUpdateMapper<Object>() {
        @Override
        public Object map(MessageCreatedUpdate update) {
            return tryHandleCommand(update);
        }

        @Override
        public Object mapDefault(Update update) {
            return handleUpdate(update);
        }
    };

    public TamTamBotBase(TamTamClient client, Object... handlers) {
        this.client = client;
        this.updateHandlers = new HashMap<>();
        this.commandHandlers = new HashMap<>();

        addHandlers(this);
        for (Object handler : handlers) {
            addHandlers(handler);
        }
    }

    @Override
    public TamTamClient getClient() {
        return client;
    }

    @Nullable
    @Override
    public Object onUpdate(Update update) {
        return update.map(rootHandler);
    }

    private static Object noResponse() {
        return null;
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

    private Object handleUpdate(Update update) {
        MethodHandle handler = updateHandlers.get(update.getClass());
        if (handler == null) {
            return null;
        }

        try {
            return handler.invoke(update);
        } catch (Throwable throwable) {
            // should never happens
            throw new RuntimeException(throwable);
        }
    }

    private Object tryHandleCommand(MessageCreatedUpdate update) {
        if (commandHandlers.isEmpty()) {
            return handleUpdate(update);
        }

        Message message = update.getMessage();
        String text = message.getBody().getText();
        if (text == null || text.isEmpty()) {
            return handleUpdate(update);
        }

        RawCommandLine commandLine = CommandLineParser.parseRaw(text);
        if (commandLine == null) {
            return handleUpdate(update);
        }

        String commandKey = commandLine.getKey();
        CommandHandle commandHandler = commandHandlers.get(commandKey);
        if (commandHandler == null) {
            return handleUpdate(update);
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
            return commandHandler.handle.invokeWithArguments(invokeArgs);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private void addHandlers(Object handler) {
        List<Class> supers = new ArrayList<>(4);
        for (Class<?> cls = handler.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            supers.add(cls);
        }

        List<Method> updateHandlers = new ArrayList<>();
        List<Method> commandHandlers = new ArrayList<>();
        for (int i = supers.size(); --i >= 0; ) {
            Class<?> cls = supers.get(i);
            for (Method m : cls.getDeclaredMethods()) {
                if (m.getAnnotation(UpdateHandler.class) != null) {
                    m.setAccessible(true);
                    updateHandlers.add(m);
                }

                if (m.getAnnotation(CommandHandler.class) != null) {
                    m.setAccessible(true);
                    commandHandlers.add(m);
                }
            }
        }

        for (Method updateHandler : updateHandlers) {
            registerUpdateHandler(updateHandler, handler);
        }

        for (Method commandHandler : commandHandlers) {
            registerCommandHandler(commandHandler, handler);
        }
    }

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

    private void registerUpdateHandler(Method method, Object target) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 1) {
            throw new IllegalArgumentException(
                    "Method " + method + " must only have single parameter of type `Update`");
        }

        Class<?> parameterType = method.getParameterTypes()[0];
        if (!Update.class.isAssignableFrom(parameterType)) {
            throw new IllegalArgumentException(
                    "Method " + method + " must have only single parameter of type `Update`");
        }

        MethodHandle mh = unreflect(method);

        @SuppressWarnings("unchecked")
        Class<? extends Update> updateClass = (Class<? extends Update>) parameterType;
        MethodHandle prev = updateHandlers.put(updateClass, mh.bindTo(target));
        if (prev != null) {
            throw new IllegalStateException(
                    "Method " + method + " overrides already existing handler for update type " + updateClass);
        }
    }


    private class CommandHandle {
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
