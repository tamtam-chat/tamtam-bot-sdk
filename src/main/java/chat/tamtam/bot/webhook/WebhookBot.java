package chat.tamtam.bot.webhook;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.TamTamBot;
import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.botapi.TamTamBotAPI;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.Subscription;
import chat.tamtam.botapi.model.SubscriptionRequestBody;

/**
 * @author alexandrchuprin
 */
public abstract class WebhookBot implements TamTamBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TamTamBotAPI api;
    private final WebhookBotOptions options;
    private final String path;

    public WebhookBot(TamTamBotAPI api, WebhookBotOptions options, String path) {
        this.api = api;
        this.options = options;
        this.path = path;
    }

    public void start(WebhookBotContainer container) throws TamTamBotException {
        if (options.shouldRemoveOldSubscriptions()) {
            try {
                unsubscribe();
            } catch (APIException | ClientException e) {
                LOG.warn("Failed to remove current subscriptions", e);
            }
        }

        SubscriptionRequestBody body = new SubscriptionRequestBody(container.getWebhookUrl(this));
        body.updateTypes(options.getUpdateTypes());

        try {
            api.subscribe(body).execute();
        } catch (APIException | ClientException e) {
            throw new TamTamBotException("Failed to start webhook bot", e);
        }
    }

    public void stop(WebhookBotContainer container) {
        if (options.shouldRemoveSubscriptionOnStop()) {
            try {
                api.unsubscribe(container.getWebhookUrl(this)).execute();
            } catch (ClientException | APIException e) {
                LOG.warn("Failed to remove current subscription", e);
            }
        }
    }

    public String getPath() {
        return path;
    }

    private void unsubscribe() throws APIException, ClientException {
        List<Subscription> subscriptions = api.getSubscriptions().execute().getSubscriptions();
        for (Subscription subscription : subscriptions) {
            api.unsubscribe(subscription.getUrl()).execute();
        }
    }
}
