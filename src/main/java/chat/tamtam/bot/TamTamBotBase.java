package chat.tamtam.bot;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.annotations.CommandHandler;
import chat.tamtam.bot.annotations.UpdateHandler;
import chat.tamtam.bot.chat.CommandLine;
import chat.tamtam.bot.chat.CommandLineParser;
import chat.tamtam.bot.updates.DefaultUpdateMapper;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.Message;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public class TamTamBotBase implements TamTamBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TamTamClient client;
    private final Map<Class<? extends Update>, MethodHandle> updateHandlers;
    private final Map<String, MethodHandle> commandHandlers;

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
        boolean hasText = text != null && !text.trim().isEmpty();
        if (!hasText) {
            return handleUpdate(update);
        }

        CommandLine commandLine = CommandLineParser.tryParse(text);
        if (commandLine == null) {
            return handleUpdate(update);
        }

        MethodHandle commandHandler = commandHandlers.get(commandLine.getKey());
        if (commandHandler == null) {
            return handleUpdate(update);
        }

        try {
            return commandHandler.invoke(message, commandLine);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private void addHandlers(Object handler) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        ArrayList<Class> supers = new ArrayList<>(4);
        for (Class<?> cls = handler.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            supers.add(cls);
        }

        MethodHandle nullResponseMH;
        try {
            nullResponseMH = lookup.findStatic(TamTamBotBase.class, "noResponse", MethodType.methodType(Object.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // actually should never happens
            throw new RuntimeException(e);
        }

        for (int i = supers.size(); --i >= 0; ) {
            Class<?> cls = supers.get(i);

            for (Method m : cls.getDeclaredMethods()) {
                m.setAccessible(true);

                UpdateHandler annotation = m.getAnnotation(UpdateHandler.class);
                if (annotation == null) {
                    continue;
                }

                Parameter[] parameters = m.getParameters();
                if (parameters.length > 1) {
                    throw new IllegalArgumentException(
                            "Method " + m + " must only have single parameter of type `Update`");
                }

                Class<?> parameterType = m.getParameterTypes()[0];
                if (!Update.class.isAssignableFrom(parameterType)) {
                    throw new IllegalArgumentException(
                            "Method " + m + " must have only single parameter of type `Update`");
                }

                MethodHandle mh;
                try {
                    mh = lookup.unreflect(m);
                } catch (IllegalAccessException e) {
                    // actually should never happens
                    throw new RuntimeException(e);
                }

                if (m.getReturnType().equals(void.class)) {
                    mh = MethodHandles.filterReturnValue(mh, nullResponseMH);
                }

                @SuppressWarnings("unchecked")
                Class<? extends Update> updateClass = (Class<? extends Update>) parameterType;
                MethodHandle prev = updateHandlers.put(updateClass, mh.bindTo(handler));
                if (prev != null) {
                    throw new IllegalStateException(
                            "Method " + m + " overrides already existing handler for update type " + updateClass);
                }
            }
        }

        for (int i = supers.size(); --i >= 0; ) {
            Class<?> cls = supers.get(i);

            for (Method m : cls.getDeclaredMethods()) {
                m.setAccessible(true);

                CommandHandler annotation = m.getAnnotation(CommandHandler.class);
                if (annotation == null) {
                    continue;
                }

                Parameter[] parameters = m.getParameters();
                if (parameters.length != 2) {
                    throw new IllegalArgumentException(
                            "Method " + m + " must match signature: (chat.tamtam.botapi.model.Message,chat.tamtam.bot" +
                                    ".chat.CommandLine)");
                }

                Class<?> parameterType1 = m.getParameterTypes()[0];
                if (!Message.class.isAssignableFrom(parameterType1)) {
                    throw new IllegalArgumentException(
                            "Method " + m + " must have only single parameter of type `Message`");
                }

                Class<?> parameterType2 = m.getParameterTypes()[1];
                if (!CommandLine.class.isAssignableFrom(parameterType2)) {
                    throw new IllegalArgumentException(
                            "Method " + m + " must have only single parameter of type `Message`");
                }

                MethodHandle commandHandler;
                try {
                    commandHandler = lookup.unreflect(m);
                } catch (IllegalAccessException e) {
                    // actually should never happens
                    throw new RuntimeException(e);
                }

                if (m.getReturnType().equals(void.class)) {
                    commandHandler = MethodHandles.filterReturnValue(commandHandler, nullResponseMH);
                }

                commandHandlers.put(annotation.value(), commandHandler.bindTo(handler));
            }
        }
    }

    private static Object noResponse() {
        return null;
    }
}
