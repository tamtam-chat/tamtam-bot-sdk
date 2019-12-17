package chat.tamtam.bot.builders;

import chat.tamtam.botapi.model.MessageLinkType;
import chat.tamtam.botapi.model.NewMessageBody;
import chat.tamtam.botapi.model.NewMessageLink;

/**
 * 🏋
 *
 * @author alexandrchuprin
 */
public class NewMessageBodyBuilder {
    private String text;
    private AttachmentsBuilder attachments;
    private NewMessageLink link;

    private NewMessageBodyBuilder(String text, AttachmentsBuilder attachments, NewMessageLink link) {
        this.text = text;
        this.attachments = attachments;
        this.link = link;
    }

    public static NewMessageBodyBuilder ofText(String text) {
        return new NewMessageBodyBuilder(text, null, null);
    }

    public static NewMessageBodyBuilder ofAttachments(AttachmentsBuilder attachmentsBuilder) {
        return new NewMessageBodyBuilder(null, attachmentsBuilder, null);
    }

    public static NewMessageBodyBuilder forward(String messageId) {
        return new NewMessageBodyBuilder(null, null, new NewMessageLink(MessageLinkType.FORWARD, messageId));
    }

    public static NewMessageBodyBuilder reply(String messageId) {
        return new NewMessageBodyBuilder(null, null, new NewMessageLink(MessageLinkType.REPLY, messageId));
    }

    public NewMessageBodyBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public NewMessageBodyBuilder withAttachments(AttachmentsBuilder attachments) {
        this.attachments = attachments;
        return this;
    }

    public NewMessageBodyBuilder withReply(String messageId) {
        this.link = new NewMessageLink(MessageLinkType.REPLY, messageId);
        return this;
    }

    public NewMessageBodyBuilder withForward(String messageId) {
        this.link = new NewMessageLink(MessageLinkType.FORWARD, messageId);
        return this;
    }

    public NewMessageBody build() {
        return new NewMessageBody(text, attachments == null ? null : attachments.build(), link);
    }
}
