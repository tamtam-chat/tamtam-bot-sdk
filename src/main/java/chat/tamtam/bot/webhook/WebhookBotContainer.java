package chat.tamtam.bot.webhook;

import chat.tamtam.bot.exceptions.TamTamBotException;

/**
 * @author alexandrchuprin
 */
public interface WebhookBotContainer {
    void register(WebhookBot bot);

    void unregister(WebhookBot bot);

    String getWebhookUrl(WebhookBot bot);

    void start() throws TamTamBotException;

    void join() throws InterruptedException;
}
