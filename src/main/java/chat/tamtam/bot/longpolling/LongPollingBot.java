package chat.tamtam.bot.longpolling;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.TamTamBot;
import chat.tamtam.botapi.TamTamBotAPI;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.Subscription;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.UpdateList;

/**
 * @author alexandrchuprin
 */
public abstract class LongPollingBot implements TamTamBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Thread poller;
    private final TamTamBotAPI api;
    private final LongPollingBotOptions options;
    private boolean isStopped;

    public LongPollingBot(TamTamBotAPI api, LongPollingBotOptions options) {
        this.poller = new Thread(this::poll, "tamtam-bot-poller-" + Objects.hashCode(api));
        this.api = api;
        this.options = options;
    }

    public void start() {
        try {
            checkWebhook();
        } catch (APIException | ClientException e) {
            LOG.error("Failed to check webhook subscriptions. Bot has not started", e);
            return;
        }

        poller.start();
    }

    public void stop() {
        isStopped = true;
        try {
            poller.interrupt();
            poller.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void handleUpdates(List<Update> updates) {
        for (Update update : updates) {
            onUpdate(update);
        }
    }

    protected UpdateList pollOnce(Long marker) throws APIException, ClientException {
        return api.getUpdates()
                .marker(marker)
                .timeout(options.getRequestTimeout())
                .types(options.getUpdateTypes())
                .limit(options.getLimit())
                .execute();
    }

    private void checkWebhook() throws ClientException, APIException {
        List<Subscription> subscriptions;
        try {
            subscriptions = api.getSubscriptions().execute().getSubscriptions();
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
            api.unsubscribe(subscription.getUrl()).execute();
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
                    LOG.info("Current polling request has been interrupted. Polling stopped.");
                    Thread.currentThread().interrupt();
                    return;
                }

                error = Math.min(++error, 5);
                LOG.error("Failed to get updates with marker {}. Will retry in {} seconds…", marker, error, e);

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(error));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }

                continue;
            }

            handleUpdates(updateList.getUpdates());
            marker = updateList.getMarker();
        }
    }
}