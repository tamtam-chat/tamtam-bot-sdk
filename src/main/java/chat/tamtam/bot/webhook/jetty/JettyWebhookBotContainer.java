package chat.tamtam.bot.webhook.jetty;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.jetbrains.annotations.NotNull;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.exceptions.WebhookException;
import chat.tamtam.bot.webhook.WebhookBot;
import chat.tamtam.bot.webhook.WebhookBotContainerBase;

/**
 * @author alexandrchuprin
 */
public class JettyWebhookBotContainer extends WebhookBotContainerBase {
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
        return String.format("http://%s%s", serverUrl, bot.getPath());
    }

    @Override
    public void start() throws TamTamBotException {
        try {
            server.start();
        } catch (Exception e) {
            throw new TamTamBotException(e);
        }

        super.start();
    }

    @Override
    public void join() throws InterruptedException {
        server.join();
        super.join();
    }

    public void stop() throws Exception {
        server.stop();
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
            try {
                String webhookResponse = handleRequest(target, request.getMethod(), request.getInputStream());
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(webhookResponse);
            } catch (WebhookException e) {
                response.sendError(e.getErrorCode(), e.getMessage());
            }
        }
    }
}
