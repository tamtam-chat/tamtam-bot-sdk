package chat.tamtam.bot.exceptions;

/**
 * @author alexandrchuprin
 */
public class BotNotFoundException extends WebhookException {
    public BotNotFoundException(String message) {
        super(404, message);
    }
}
