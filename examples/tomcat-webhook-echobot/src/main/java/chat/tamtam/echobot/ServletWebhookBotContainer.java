package chat.tamtam.echobot;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chat.tamtam.bot.exceptions.WebhookException;
import chat.tamtam.bot.webhook.WebhookBot;
import chat.tamtam.bot.webhook.WebhookBotContainerBase;

/**
 * @author alexandrchuprin
 */
public class ServletWebhookBotContainer extends WebhookBotContainerBase implements Servlet {
    private final String serverUrl;
    private final HttpServlet servlet;

    ServletWebhookBotContainer(String serverUrl) {
        this.serverUrl = serverUrl;
        this.servlet = new DelegatingBotServlet();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        servlet.init(servletConfig);
    }

    @Override
    public ServletConfig getServletConfig() {
        return servlet.getServletConfig();
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException,
            IOException {
        servlet.service(servletRequest, servletResponse);
    }

    @Override
    public String getServletInfo() {
        return servlet.getServletInfo();
    }

    @Override
    public void destroy() {
        servlet.destroy();
    }

    @Override
    public String getWebhookUrl(WebhookBot bot) {
        return String.format("https://%s/%s/%s", serverUrl, "bots", bot.getKey());
    }

    private class DelegatingBotServlet extends HttpServlet {
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String webhookResponse;
            try {
                webhookResponse = handleRequest(req.getPathInfo(), req.getMethod(), req.getInputStream());
            } catch (WebhookException e) {
                resp.sendError(e.getErrorCode(), e.getMessage());
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            if (webhookResponse == null) {
                return;
            }

            resp.getWriter().println(webhookResponse);
        }
    }
}
