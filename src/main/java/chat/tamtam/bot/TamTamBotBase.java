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

import chat.tamtam.bot.annotations.UpdateHandler;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public class TamTamBotBase implements TamTamBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TamTamClient client;
    private final Map<Class<? extends Update>, MethodHandle> updateHandlers;

    protected TamTamBotBase(TamTamClient client, Object... handlers) {
        this.client = client;
        this.updateHandlers = new HashMap<>();

        addUpdateHandlers(this);
        for (Object handler : handlers) {
            addUpdateHandlers(handler);
        }
    }

    @Override
    public TamTamClient getClient() {
        return client;
    }

    @Nullable
    @Override
    public Object onUpdate(Update update) {
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

    private void addUpdateHandlers(Object handler) {
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
                    LOG.warn("Method {} overrides already existing handler for update type {}", m, updateClass);
                }
            }
        }
    }

    private static Object noResponse() {
        return null;
    }
}
