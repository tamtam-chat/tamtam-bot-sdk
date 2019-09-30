package chat.tamtam.logbot;

import chat.tamtam.bot.webhook.jetty.JettyWebhookBotContainer;
import chat.tamtam.botapi.TamTamBotAPI;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {
    private static final Options OPTIONS = new Options();

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
        int port = OPTIONS.port.value(optionSet);

        JettyWebhookBotContainer container = new JettyWebhookBotContainer("0.0.0.0", port);
        TamTamBotAPI api = TamTamBotAPI.create(accessToken);
        LogBot bot = new LogBot(api);
        container.register(bot);
        container.start();
        container.join();
    }

    private static class Options extends OptionParser {
        OptionSpec<String> accessToken = accepts("token")
                .withRequiredArg()
                .required()
                .ofType(String.class);

        OptionSpec<Integer> port = accepts("port")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(20998);
    }
}
