package chat.tamtam.bot.longpolling;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.TamTamBot;
import chat.tamtam.bot.TamTamBotBase;
import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.Subscription;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.UpdateList;
import chat.tamtam.botapi.queries.GetSubscriptionsQuery;
import chat.tamtam.botapi.queries.GetUpdatesQuery;
import chat.tamtam.botapi.queries.UnsubscribeQuery;

/**
 * @author alexandrchuprin
 */
public abstract class LongPollingBot extends TamTamBotBase implements TamTamBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Thread poller;
    private final LongPollingBotOptions options;
    private volatile boolean isStopped;

    public LongPollingBot(TamTamClient client, LongPollingBotOptions options) {
        super(client);
        this.poller = new Thread(this::poll, "tamtam-bot-poller-" + Objects.hashCode(this));
        this.options = options;
    }

    public void start() throws TamTamBotException {
        try {
            checkWebhook();
        } catch (Exception e) {
            throw new TamTamBotException(e.getMessage(), e);
        }

        poller.start();
    }

    public void stop() throws InterruptedException {
        isStopped = true;
        poller.join();
    }

    protected void handleUpdates(List<Update> updates) {
        for (Update update : updates) {
            onUpdate(update);
        }
    }

    protected UpdateList pollOnce(Long marker) throws APIException, ClientException {
        return new GetUpdatesQuery(getClient())
                .marker(marker)
                .timeout(options.getRequestTimeout())
                .types(options.getUpdateTypes())
                .limit(options.getLimit())
                .execute();
    }

    private void checkWebhook() throws APIException, ClientException {
        List<Subscription> subscriptions;
        try {
            subscriptions = new GetSubscriptionsQuery(getClient()).execute().getSubscriptions();
        } catch (APIException | ClientException e) {
            LOG.error("Failed to check bot subscription", e);
            return;
        }

        if (subscriptions.isEmpty()) {
            return;
        }

        if (!options.shouldRemoveWebhook()) {
            throw new IllegalStateException(String.format("Bot %s has webhook subscriptions: %s. " +
                    "Long polling will not receive updates in this case." +
                    "Remove it manually or set `shouldRemoveWebhook` to `true` in options.", this, subscriptions));
        }

        for (Subscription subscription : subscriptions) {
            new UnsubscribeQuery(getClient(), subscription.getUrl()).execute();
        }
    }

    private void poll() {
        Long marker = null;
        int error = 0;
        while (!isStopped) {
            UpdateList updateList;
            try {
                updateList = pollOnce(marker);
                error = 0;
            } catch (APIException | ClientException e) {
                if (e.getCause() instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }

                error = Math.min(++error, 5);
                LOG.error("Failed to get updates with marker {}. Will retry in {} seconds…", marker, error, e);

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(error));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }

                continue;
            }

            handleUpdates(updateList.getUpdates());
            marker = updateList.getMarker();
        }

        LOG.info("Polling thread stopped");
    }
}