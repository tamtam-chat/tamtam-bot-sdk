package chat.tamtam.echobot;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.webhook.WebhookBot;
import chat.tamtam.bot.webhook.WebhookBotOptions;
import chat.tamtam.bot.webhook.jetty.JettyWebhookBotContainer;
import chat.tamtam.botapi.model.Update;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class WebhookEchoBot extends WebhookBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Options OPTIONS = new Options();
    private static final int DEFAULT_PORT = 12945;

    private final EchoHandler handler;

    private WebhookEchoBot(String accessToken) {
        super(accessToken, WebhookBotOptions.DEFAULT);
        this.handler = new EchoHandler(getClient());
    }

    public static void main(String[] args) throws Exception {
        OptionSet optionSet;
        try {
            optionSet = OPTIONS.parse(args);
        } catch (OptionException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        String accessToken = OPTIONS.accessToken.value(optionSet);
        WebhookEchoBot bot = new WebhookEchoBot(accessToken);

        int port = OPTIONS.port.value(optionSet);
        String hostname = OPTIONS.host.value(optionSet);

        JettyWebhookBotContainer botContainer = new JettyWebhookBotContainer(hostname, DEFAULT_PORT);
        botContainer.register(bot);

        Runtime.getRuntime().addShutdownHook(new Thread(botContainer::stop));
        botContainer.start();
        botContainer.join();
    }

    @Override
    public Object onUpdate(Update update) {
        LOG.info("Handling update: {}", update);
        update.visit(handler);
        return null;
    }

    private static class Options extends OptionParser {
        OptionSpec<String> accessToken = accepts("token")
                .withRequiredArg()
                .required()
                .ofType(String.class);

        OptionSpec<String> host = accepts("host")
                .withRequiredArg()
                .defaultsTo("0.0.0.0")
                .ofType(String.class);

        OptionSpec<Integer> port = accepts("port")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(DEFAULT_PORT);
    }
}
