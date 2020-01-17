package chat.tamtam.bot.webhook.jetty;

import java.lang.invoke.MethodHandles;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.webhook.WebhookBot;
import chat.tamtam.bot.webhook.WebhookBotContainerBase;

/**
 * @author alexandrchuprin
 */
public class JettyWebhookBotContainer extends WebhookBotContainerBase {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Server server;
    private final String serverUrl;

    public JettyWebhookBotContainer(String serverUrl, int port) {
        this(serverUrl, new Server(port));
    }

    public JettyWebhookBotContainer(String serverUrl, Server server) {
        this.server = server;
        this.serverUrl = serverUrl;
        this.server.setHandler(prepareHandler(server));
    }

    @Override
    public String getWebhookUrl(WebhookBot bot) {
        return String.format("http://%s%s", serverUrl, bot.getKey());
    }

    @Override
    public void start() {
        try {
            server.start();
        } catch (Exception e) {
            LOG.error("Failed to start webhook server", e);
            return;
        }

        super.start();
    }

    @Override
    public void stop() {
        try {
            super.stop();
            server.stop();
        } catch (Exception e) {
            LOG.error("Failed to stop webhook server", e);
        }
    }

    public void join() throws InterruptedException {
        server.join();
    }

    @NotNull
    private Handler prepareHandler(Server server) {
        Handler currentHandler = server.getHandler();
        WebhookDispatcher webhookDispatcherHandler = new WebhookDispatcher(this);
        if (currentHandler == null) {
            return webhookDispatcherHandler;
        }

        return new HandlerList(webhookDispatcherHandler, currentHandler);
    }

}
