package chat.tamtam.bot;

import java.util.List;

import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.Message;
import chat.tamtam.botapi.model.MessageBody;

/**
 * @author alexandrchuprin
 */
public class Mocks {
    public static Message message(String text) {
        return message(text, null);
    }

    public static Message message(String text, List<Attachment> attachments) {
        MessageBody body = new MessageBody(null, null, text, attachments);
        return new Message(null, null, body);
    }
}
