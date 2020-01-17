package chat.tamtam.echobot;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBot;
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
        LongPollingBot bot = new EchoBot(accessToken);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping bot…");
            bot.stop();
        }));

        try {
            bot.start();
        } catch (TamTamBotException e) {
            System.err.println("Failed to start bot: " + e.getMessage());
            System.exit(1);
        }
    }

    private static class Options extends OptionParser {
        OptionSpec<String> accessToken = accepts("token")
                .withRequiredArg()
                .required()
                .ofType(String.class);
    }
}
