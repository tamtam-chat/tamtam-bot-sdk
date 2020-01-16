package chat.tamtam.echobot;

import java.lang.invoke.MethodHandles;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.exceptions.TamTamBotException;
import chat.tamtam.bot.longpolling.LongPollingBot;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public class EchoBot extends LongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EchoHandler handler;

    EchoBot(String accessToken) {
        super(accessToken, LongPollingBotOptions.DEFAULT);
        this.handler = new EchoHandler(getClient());
    }

    @Nullable
    @Override
    public Object onUpdate(Update update) {
        LOG.info("Received update: {}", update);
        update.visit(handler);
        return null;
    }

    @Override
    public void start() throws TamTamBotException {
        super.start();
        LOG.info("Bot started");
    }

    @Override
    public void stop() {
        super.stop();
        LOG.info("Bot stopped");
    }
}
