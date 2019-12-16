package chat.tamtam.bot.exceptions;

/**
 * @author alexandrchuprin
 */
public class WebhookException extends Exception {
    private final int errorCode;

    WebhookException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    private WebhookException(int errorCode, String message, Exception exception) {
        super(message, exception);
        this.errorCode = errorCode;
    }

    public static WebhookException internalServerError(String message) {
        return new WebhookException(503, message);
    }

    public static WebhookException internalServerError(String message, Exception exception) {
        return new WebhookException(503, message, exception);
    }

    public int getErrorCode() {
        return errorCode;
    }
}
