package chat.tamtam.bot;

import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.NewMessageBody;
import chat.tamtam.botapi.model.NewMessageLink;
import chat.tamtam.botapi.model.SendMessageResult;
import chat.tamtam.botapi.model.TextFormat;
import chat.tamtam.botapi.queries.SendMessageQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * @author almaz.shakirov
 */
public class MessageSender {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Max characters in message text,
     * see <a href="https://dev.tamtam.chat/#operation/sendMessage">Send Message (Bot API)</a>
     */
    private static final int MAX_CHARS_IN_MESSAGE = 4000;

    private final TamTamClient client;

    public MessageSender(TamTamClient client) {
        this.client = client;
    }

    /**
     * If messageBody has text with length more than {@link MessageSender#MAX_CHARS_IN_MESSAGE}, then it will be automatically
     * split and sent as several messages.
     * @param userId recipient
     * @param messageBody message
     */
    public void sendMessage(long userId, NewMessageBody messageBody) {
        if (messageBody.getText() == null || messageBody.getText().length() <= 4000) { // 4000 - max characters in message text
            try {
                new SendMessageQuery(client, messageBody).userId(userId).enqueue();
            } catch (ClientException e) {
                LOG.error("Failed to send message to user=" + userId, e);
            }
        }

        String[] splitText = splitTextByMaxCharsInMessage(messageBody.getText());
        try {
            Boolean notify = messageBody.isNotify();
            TextFormat format = messageBody.getFormat();

            NewMessageBody msg = createNewMessageBody(splitText[0], messageBody.getAttachments(), messageBody.getLink(), notify, format);
            sendMessageWithResult(userId, msg);
            for (int i = 1; i < splitText.length; i++) {
                sendMessageWithResult(userId, createNewMessageBody(splitText[i], null, null, notify, format));
            }
        } catch (APIException | ClientException e) {
            LOG.error("Failed to send message to user=" + userId, e);
        }
    }

    public SendMessageResult sendMessageWithResult(long userId, NewMessageBody messageBody) throws APIException, ClientException {
        return new SendMessageQuery(client, messageBody).userId(userId).execute();
    }

    /**
     * @param text text to send
     * @return text split by {@link MessageSender#MAX_CHARS_IN_MESSAGE} chars per cell
     * @see MessageSender#MAX_CHARS_IN_MESSAGE
     */
    public static String[] splitTextByMaxCharsInMessage(String text) {
        String[] split = new String[text.length() / MAX_CHARS_IN_MESSAGE + 1];
        StringBuilder sb = new StringBuilder(text);

        for (int i = 0; i < split.length; i++) {
            int endIndex = getEndIndexOfMaxAvailableSubstringToSend(sb);
            split[i] = sb.substring(0, endIndex + 1);
            sb.delete(0, endIndex + 1);
        }

        return split;
    }

    private NewMessageBody createNewMessageBody(String text, List<AttachmentRequest> attachments, NewMessageLink link, Boolean notify, TextFormat format) {
        NewMessageBody newMessageBody = new NewMessageBody(text, attachments, link);
        newMessageBody.setNotify(notify);
        newMessageBody.setFormat(format);
        return newMessageBody;
    }

    private static int getEndIndexOfMaxAvailableSubstringToSend(StringBuilder sb) {
        if (sb.length() <= MAX_CHARS_IN_MESSAGE) {
            return sb.length() - 1;
        }

        // if the last char is not surrogate, then it isn't an emoji and we can return
        if (!Character.isSurrogate(sb.charAt(MAX_CHARS_IN_MESSAGE - 1)) && sb.charAt(MAX_CHARS_IN_MESSAGE - 1) != 8205) {
            return MAX_CHARS_IN_MESSAGE - 1;
        }

        return getEndIndexWithoutSplittingEmoji(sb);
    }

    private static int getEndIndexWithoutSplittingEmoji(StringBuilder sb) {
        // If so, then an emoji fits completely into the substring [0, maxCharsInMessage).
        if (Character.isLowSurrogate(sb.charAt(MAX_CHARS_IN_MESSAGE - 1))
                && sb.charAt(MAX_CHARS_IN_MESSAGE) != 8205) { // 8205 - Zero Width Joiner
            return MAX_CHARS_IN_MESSAGE - 1;
        }

        // if we are here, then there is an emoji, which has unicode code units before char with index maxCharsInMessage
        // and has after this char
        for (int i = MAX_CHARS_IN_MESSAGE - 2; i > 0; i--) {
            if (!Character.isSurrogate(sb.charAt(i)) && sb.charAt(i) != 8205) {
                return i;
            }

            if (Character.isLowSurrogate(sb.charAt(i)) && sb.charAt(i + 1) != 8205 && sb.charAt(i - 1) != 8205) {
                return i;
            }
        }

        // if we are here, then there is something wrong with this string
        return MAX_CHARS_IN_MESSAGE - 1;
    }
}
