package chat.tamtam.echobot;

import chat.tamtam.bot.longpolling.LongPollingBot;
import chat.tamtam.botapi.TamTamBotAPI;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {
    private static final Options OPTIONS = new Options();

    public static void main(String[] args) {
        OptionSet optionSet;
        try {
            optionSet = OPTIONS.parse(args);
        } catch (OptionException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }

        String accessToken = OPTIONS.accessToken.value(optionSet);
        TamTamBotAPI api = TamTamBotAPI.create(accessToken);
        LongPollingBot bot = new EchoBot(api);

        Runtime.getRuntime().addShutdownHook(new Thread(bot::stop));
        bot.start();
    }

    private static class Options extends OptionParser {
        OptionSpec<String> accessToken = accepts("token")
                .withRequiredArg()
                .required()
                .ofType(String.class);
    }
}
