package chat.tamtam.bot.builders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.Button;
import chat.tamtam.botapi.model.CallbackButton;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequestPayload;
import chat.tamtam.botapi.model.MessageLinkType;
import chat.tamtam.botapi.model.NewMessageBody;
import chat.tamtam.botapi.model.NewMessageLink;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

import static chat.tamtam.bot.builders.AttachmentsBuilder.inlineKeyboard;
import static chat.tamtam.bot.builders.InlineKeyboardBuilder.singleRow;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author alexandrchuprin
 */
public class NewMessageBodyBuilderTest {
    private Button button1 = new CallbackButton("1", "1");
    private Button button2 = new CallbackButton("2", "2");
    private AttachmentsBuilder attachments = AttachmentsBuilder
            .videos("1", "2", "3")
            .with(inlineKeyboard(singleRow(button1, button2)));

    private List<AttachmentRequest> expectedAttachments = Arrays.asList(
            new VideoAttachmentRequest(new UploadedInfo().token("1")),
            new VideoAttachmentRequest(new UploadedInfo().token("2")),
            new VideoAttachmentRequest(new UploadedInfo().token("3")),
            new InlineKeyboardAttachmentRequest(new InlineKeyboardAttachmentRequestPayload(
                    Collections.singletonList(Arrays.asList(button1, button2))))
    );

    @Test
    public void test1() {
        String text = "test";
        NewMessageBody newMessageBody = NewMessageBodyBuilder.ofText(text)
                .withAttachments(attachments)
                .build();

        assertThat(newMessageBody.getText(), is(text));
        assertThat(newMessageBody.getLink(), is(nullValue()));
        assertThat(newMessageBody.getAttachments(), is(expectedAttachments));
    }

    @Test
    public void test2() {
        String text = "text2";
        NewMessageBody body = NewMessageBodyBuilder.ofAttachments(attachments)
                .withText(text)
                .build();

        assertThat(body.getText(), is(text));
        assertThat(body.getLink(), is(nullValue()));
        assertThat(body.getAttachments(), is(expectedAttachments));
    }

    @Test
    public void test3() {
        String text = "text2";
        String messageId = "messageId";
        NewMessageBody body = NewMessageBodyBuilder.forward(messageId)
                .withAttachments(attachments)
                .withText(text)
                .build();

        assertThat(body.getText(), is(text));
        assertThat(body.getLink(), is(new NewMessageLink(MessageLinkType.FORWARD, messageId)));
        assertThat(body.getAttachments(), is(expectedAttachments));
    }

    @Test
    public void test4() {
        String text = "text2";
        String messageId = "messageId";
        NewMessageBody body = NewMessageBodyBuilder.reply(messageId)
                .withAttachments(attachments)
                .withText(text)
                .build();

        assertThat(body.getText(), is(text));
        assertThat(body.getLink(), is(new NewMessageLink(MessageLinkType.REPLY, messageId)));
        assertThat(body.getAttachments(), is(expectedAttachments));
    }

    @Test
    public void test5() {
        String messageId = "messageId";
        NewMessageBody body = NewMessageBodyBuilder.ofAttachments(attachments)
                .withReply(messageId)
                .build();

        assertThat(body.getLink(), is(new NewMessageLink(MessageLinkType.REPLY, messageId)));
        assertThat(body.getAttachments(), is(expectedAttachments));
    }

    @Test
    public void test6() {
        String messageId = "messageId";
        NewMessageBody body = NewMessageBodyBuilder.ofAttachments(attachments)
                .withForward(messageId)
                .build();

        assertThat(body.getLink(), is(new NewMessageLink(MessageLinkType.FORWARD, messageId)));
        assertThat(body.getAttachments(), is(expectedAttachments));
    }
}