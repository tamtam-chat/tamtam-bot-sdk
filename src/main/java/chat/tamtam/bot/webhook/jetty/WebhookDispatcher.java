package chat.tamtam.bot.webhook.jetty;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.exceptions.BotNotFoundException;
import chat.tamtam.bot.exceptions.WebhookException;
import chat.tamtam.bot.webhook.WebhookBotContainer;

/**
 * @author alexandrchuprin
 */
class WebhookDispatcher extends AbstractHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private WebhookBotContainer botContainer;

    WebhookDispatcher(WebhookBotContainer botContainer) {
        this.botContainer = botContainer;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException {

        String webhookResponse;
        try {
            webhookResponse = botContainer.handleRequest(target, request.getMethod(), request.getInputStream());
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
