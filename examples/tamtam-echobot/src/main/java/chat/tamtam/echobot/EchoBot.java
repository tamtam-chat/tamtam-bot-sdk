package chat.tamtam.echobot;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.longpolling.LongPollingBot;
import chat.tamtam.bot.longpolling.LongPollingBotOptions;
import chat.tamtam.botapi.TamTamBotAPI;
import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public class EchoBot extends LongPollingBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EchoBotUpdateHandler handler;

    EchoBot(TamTamBotAPI api) {
        super(api, LongPollingBotOptions.DEFAULT);
        this.handler = new EchoBotUpdateHandler(api);
    }

    @Override
    public void start() {
        super.start();
        LOG.info("Bot started");
    }

    @Override
    public void stop() {
        super.stop();
        LOG.info("Bot stopped");
    }

    @Override
    public void onUpdate(Update update) {
        LOG.info("Handling update: {}", update);
        update.visit(handler);
    }
}
