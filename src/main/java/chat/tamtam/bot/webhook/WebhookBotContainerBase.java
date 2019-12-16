package chat.tamtam.bot.webhook;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.TamTamBot;
import chat.tamtam.bot.exceptions.BotNotFoundException;
import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.exceptions.WebhookException;
import chat.tamtam.botapi.client.TamTamSerializer;
import chat.tamtam.botapi.exceptions.SerializationException;
import chat.tamtam.botapi.model.Update;

/**
 * Base implementation of {@link WebhookBotContainer} that registers bots in map, parses incoming requests as
 * {@link Update} and delegates handling to {@link WebhookBot}.
 *
 * @author alexandrchuprin
 */
public abstract class WebhookBotContainerBase implements WebhookBotContainer {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, WebhookBot> bots = new ConcurrentHashMap<>();

    @Override
    public void register(WebhookBot bot) {
        if (bots.putIfAbsent(getPath(bot), bot) != null) {
            throw new IllegalStateException("Bot " + bot + " is already registered");
        }
    }

    @Override
    public void unregister(WebhookBot bot) {
        if (!bots.remove(getPath(bot), bot)) {
            throw new IllegalStateException("Bot " + bot + " is not registered");
        }
    }

    @Override
    public Iterable<WebhookBot> getBots() {
        return bots.values();
    }

    @Override
    public void start() throws TamTamBotException {
        for (WebhookBot bot : bots.values()) {
            try {
                String url = bot.start(this);
                LOG.info("Bot {} registered webhook URL: {}", bot, url);
            } catch (TamTamBotException e) {
                LOG.error("Failed to start bot {}", bot, e);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        for (WebhookBot bot : bots.values()) {
            bot.stop(this);
            LOG.info("Bot {} stopped", bot);
        }
    }

    @Override
    public String handleRequest(String path, String method, InputStream body) throws WebhookException {
        if (!method.equals("POST")) {
            return "OK";
        }

        TamTamBot bot = bots.get(path);
        if (bot == null) {
            throw new BotNotFoundException("No bot registered by path: " + path);
        }

        TamTamSerializer serializer = bot.getClient().getSerializer();
        Update update;
        try {
            update = serializer.deserialize(body, Update.class);
        } catch (SerializationException e1) {
            throw WebhookException.internalServerError("Failed to parse update: " + body, e1);
        }

        Object response = bot.onUpdate(update);

        try {
            return serializer.serializeToString(response);
        } catch (SerializationException e) {
            throw WebhookException.internalServerError("Failed to serialize response: " + response, e);
        }
    }

    /**
     * @return full bot HTTP path inside container
     */
    protected String getPath(WebhookBot bot) {
        return "/" + bot.getKey();
    }
}
