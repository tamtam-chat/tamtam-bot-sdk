package chat.tamtam.bot.webhook;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.TamTamBot;
import chat.tamtam.bot.TamTamBotBase;
import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.SimpleQueryResult;
import chat.tamtam.botapi.model.Subscription;
import chat.tamtam.botapi.model.SubscriptionRequestBody;
import chat.tamtam.botapi.queries.GetSubscriptionsQuery;
import chat.tamtam.botapi.queries.SubscribeQuery;
import chat.tamtam.botapi.queries.UnsubscribeQuery;

/**
 * @author alexandrchuprin
 */
public class WebhookBot extends TamTamBotBase implements TamTamBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final WebhookBotOptions options;
    private final AtomicBoolean running = new AtomicBoolean();

    public WebhookBot(String accessToken, Object... handlers) {
        this(accessToken, WebhookBotOptions.DEFAULT, handlers);
    }

    public WebhookBot(String accessToken, WebhookBotOptions options, Object... handlers) {
        this(TamTamClient.create(accessToken), options, handlers);
    }

    public WebhookBot(TamTamClient client, WebhookBotOptions options, Object... handlers) {
        super(client, handlers);
        this.options = options;
    }

    /**
     * @return true if bot started successfully
     */
    public boolean start(WebhookBotContainer container) throws TamTamBotException {
        if (!running.compareAndSet(false, true)) {
            return false;
        }

        if (options.shouldRemoveOldSubscriptions()) {
            try {
                unsubscribeAll();
            } catch (APIException | ClientException e) {
                LOG.warn("Failed to remove current subscriptions", e);
            }
        }

        String webhookUrl = container.getWebhookUrl(this);
        SubscriptionRequestBody body = new SubscriptionRequestBody(webhookUrl);
        body.updateTypes(options.getUpdateTypes());

        try {
            new SubscribeQuery(getClient(), body).execute();
        } catch (APIException | ClientException e) {
            throw new TamTamBotException("Failed to start webhook bot", e);
        }

        return true;
    }

    /**
     * @return false if bot is not running
     */
    public boolean stop(WebhookBotContainer container) {
        return running.compareAndSet(true, false);
    }

    /**
     * Should return unique key across all bots in container. Returns access token by default.
     */
    public String getKey() {
        return getClient().getAccessToken();
    }

    public boolean isRunning() {
        return running.get();
    }

    private void unsubscribeAll() throws APIException, ClientException {
        List<Subscription> subscriptions = new GetSubscriptionsQuery(getClient()).execute().getSubscriptions();
        for (Subscription subscription : subscriptions) {
            SimpleQueryResult result = new UnsubscribeQuery(getClient(), subscription.getUrl()).execute();
            if (!result.isSuccess()) {
                LOG.warn("Failed to remove subscription {}. Reason: {}", subscription.getUrl(), result.getMessage());
            }
        }
    }
}
