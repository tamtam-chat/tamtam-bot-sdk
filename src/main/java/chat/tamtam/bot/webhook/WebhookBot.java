package chat.tamtam.bot.webhook;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.TamTamBot;
import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.Subscription;
import chat.tamtam.botapi.model.SubscriptionRequestBody;
import chat.tamtam.botapi.queries.GetSubscriptionsQuery;
import chat.tamtam.botapi.queries.SubscribeQuery;
import chat.tamtam.botapi.queries.UnsubscribeQuery;

/**
 * @author alexandrchuprin
 */
public abstract class WebhookBot implements TamTamBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TamTamClient client;
    private final WebhookBotOptions options;

    public WebhookBot(TamTamClient client, WebhookBotOptions options) {
        this.client = client;
        this.options = options;
    }

    @Override
    public TamTamClient getClient() {
        return client;
    }

    /**
     * @return registered webhook URL
     */
    public String start(WebhookBotContainer container) throws TamTamBotException {
        if (options.shouldRemoveOldSubscriptions()) {
            try {
                unsubscribe();
            } catch (APIException | ClientException e) {
                LOG.warn("Failed to remove current subscriptions", e);
            }
        }

        String webhookUrl = container.getWebhookUrl(this);
        SubscriptionRequestBody body = new SubscriptionRequestBody(webhookUrl);
        body.updateTypes(options.getUpdateTypes());

        try {
            new SubscribeQuery(client, body).execute();
        } catch (APIException | ClientException e) {
            throw new TamTamBotException("Failed to start webhook bot", e);
        }

        return webhookUrl;
    }

    public void stop(WebhookBotContainer container) {
        // do nothing by default
    }

    /**
     * Should return unique key across all bots in container. Returns access token by default.
     */
    public String getKey() {
        return client.getAccessToken();
    }

    private void unsubscribe() throws APIException, ClientException {
        List<Subscription> subscriptions = new GetSubscriptionsQuery(client).execute().getSubscriptions();
        for (Subscription subscription : subscriptions) {
            new UnsubscribeQuery(client, subscription.getUrl()).execute();
        }
    }
}
