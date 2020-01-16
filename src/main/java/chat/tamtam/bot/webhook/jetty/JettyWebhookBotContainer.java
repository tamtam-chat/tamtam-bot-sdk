package chat.tamtam.bot.webhook.jetty;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.exceptions.BotNotFoundException;
import chat.tamtam.bot.exceptions.WebhookException;
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
            server.stop();
            server.join();
            super.stop();
        } catch (Exception e) {
            LOG.error("Failed to stop webhook server", e);
        }
    }

    @NotNull
    private Handler prepareHandler(Server server) {
        Handler currentHandler = server.getHandler();
        WebhookDispatcher webhookDispatcherHandler = new WebhookDispatcher();
        if (currentHandler == null) {
            return webhookDispatcherHandler;
        }

        return new HandlerList(webhookDispatcherHandler, currentHandler);
    }

    private class WebhookDispatcher extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request,
                           HttpServletResponse response) throws IOException {

            String webhookResponse;
            try {
                webhookResponse = handleRequest(target, request.getMethod(), request.getInputStream());
            } catch (BotNotFoundException e) {
                LOG.warn(e.getMessage());
                response.sendError(e.getErrorCode(), e.getMessage());
                return;
            } catch (WebhookException e) {
                String errorId = Long.toHexString(ThreadLocalRandom.current().nextLong());
                LOG.error("Error happend while handling request: {}. Error ID: {}", request, errorId, e);
                response.sendError(e.getErrorCode(), "Error ID: " + errorId);
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            if (webhookResponse == null) {
                return;
            }

            response.getWriter().println(webhookResponse);
        }
    }
}
