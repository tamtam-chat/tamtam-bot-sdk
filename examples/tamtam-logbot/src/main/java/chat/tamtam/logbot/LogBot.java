package chat.tamtam.logbot;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.tamtam.bot.webhook.WebhookBot;
import chat.tamtam.bot.webhook.WebhookBotOptions;
import chat.tamtam.botapi.TamTamBotAPI;
import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public class LogBot extends WebhookBot {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final WebhookBotOptions OPTIONS = WebhookBotOptions.DEFAULT;

    public LogBot(TamTamBotAPI api) {
        super(api, OPTIONS, "/logbot");
    }

    @Override
    public void onUpdate(Update update) {
        LOG.info("Received update: " + update);
    }
}
