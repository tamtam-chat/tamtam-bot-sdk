package chat.tamtam.bot.exceptions;

/**
 * @author alexandrchuprin
 */
public class TamTamBotException extends Exception {
    public TamTamBotException(Throwable cause) {
        super(cause);
    }

    public TamTamBotException(String message, Exception cause) {
        super(message, cause);
    }
}
