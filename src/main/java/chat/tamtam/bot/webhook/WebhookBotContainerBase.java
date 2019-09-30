package chat.tamtam.bot.webhook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.TamTamBot;
import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.exceptions.WebhookException;
import chat.tamtam.botapi.client.impl.JacksonSerializer;
import chat.tamtam.botapi.exceptions.SerializationException;
import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public abstract class WebhookBotContainerBase implements WebhookBotContainer {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final JacksonSerializer SERIALIZER = new JacksonSerializer();

    private final Map<String, WebhookBot> bots = new ConcurrentHashMap<>();

    @Override
    public void register(WebhookBot bot) {
        if (bots.putIfAbsent(bot.getPath(), bot) != null) {
            throw new IllegalStateException("Bot " + bot + " is already registered");
        }
    }

    @Override
    public void unregister(WebhookBot bot) {
        if (!bots.remove(bot.getPath(), bot)) {
            throw new IllegalStateException("Bot " + bot + " is not registered");
        }
    }

    @Override
    public void start() throws TamTamBotException {
        for (WebhookBot bot : bots.values()) {
            try {
                bot.start(this);
            } catch (TamTamBotException e) {
                LOG.error("Failed to start bot {}", bot, e);
            }
        }
    }

    @Override
    public void join() throws InterruptedException {
        for (WebhookBot bot : bots.values()) {
            bot.stop(this);
        }
    }

    protected String handleRequest(String path, String method, InputStream body) throws WebhookException {
        TamTamBot bot = bots.get(path);
        if (bot == null) {
            throw WebhookException.notFound("No bot registered by path: " + path);
        }

        if (!method.equals("POST")) {
            return "OK";
        }

        String requestBody;
        try {
            requestBody = readBody(body);
        } catch (IOException e) {
            throw WebhookException.internalServerError("Failed to read request body");
        }

        Update update;
        try {
            update = SERIALIZER.deserialize(requestBody, Update.class);
        } catch (SerializationException e) {
            LOG.error("Failed to parse update: {}", requestBody, e);
            throw WebhookException.internalServerError("Failed to parse update: " + requestBody);
        }

        bot.onUpdate(update);
        return requestBody;
    }

    private String readBody(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString(StandardCharsets.UTF_8.name());
    }
}
