package chat.tamtam.bot.webhook;

import java.io.InputStream;

import org.jetbrains.annotations.Nullable;

import chat.tamtam.bot.exceptions.WebhookException;

/**
 * @author alexandrchuprin
 */
public interface WebhookBotContainer {
    /**
     * Registers bot in container.
     */
    void register(WebhookBot bot);

    /**
     * Removes bot from container.
     */
    void unregister(WebhookBot bot);

    /**
     * @return all bots registered in container.
     */
    Iterable<WebhookBot> getBots();

    /**
     * @return URL that handles webhook requests for bot.
     */
    String getWebhookUrl(WebhookBot bot);

    /**
     * Starts container (underlying server if it required, registered bots).
     */
    void start();

    /**
     * Stops container (server, bots).
     */
    void stop();

    /**
     * Accepts incoming HTTP request and delegates it to bot if it is valid webhook update.
     *
     * @param path   full path of request
     * @param method HTTP-method
     * @param body   request body
     * @return response. Can be `null` if bot doesn't send any response to Bot API.
     */
    @Nullable
    String handleRequest(String path, String method, InputStream body) throws WebhookException;
}
