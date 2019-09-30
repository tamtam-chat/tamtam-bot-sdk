package chat.tamtam.bot.exceptions;

/**
 * @author alexandrchuprin
 */
public class WebhookException extends Exception {
    private final int errorCode;
    private final String message;

    private WebhookException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static WebhookException notFound(String message) {
        return new WebhookException(404, message);
    }

    public static WebhookException internalServerError(String message) {
        return new WebhookException(503, message);
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
