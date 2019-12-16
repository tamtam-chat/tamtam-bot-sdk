package chat.tamtam.bot.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import chat.tamtam.bot.builders.attachments.PhotosBuilder;
import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.Button;
import chat.tamtam.botapi.model.CallbackButton;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequestPayload;
import chat.tamtam.botapi.model.PhotoAttachment;
import chat.tamtam.botapi.model.PhotoAttachmentPayload;
import chat.tamtam.botapi.model.PhotoAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachmentRequestPayload;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

import static chat.tamtam.bot.builders.AttachmentsBuilder.inlineKeyboard;
import static chat.tamtam.bot.builders.AttachmentsBuilder.videos;
import static chat.tamtam.bot.builders.InlineKeyboardBuilder.single;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author alexandrchuprin
 */
public class AttachmentsBuilderTest {
    @Test
    public void test1() {
        Button button = new CallbackButton("1", "1");
        Button button2 = new CallbackButton("2", "2");
        Button button3 = new CallbackButton("3", "3");

        List<AttachmentRequest> attachments = AttachmentsBuilder
                .photos("123", "345")
                .with(PhotosBuilder.byUrls("photoUrl"))
                .with(videos("678"))
                .with(inlineKeyboard(single(button).addRow(button2, button3)))
                .build();

        List<AttachmentRequest> expected = new ArrayList<>();
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token("123")));
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token("345")));
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().url("photoUrl")));
        expected.add(new VideoAttachmentRequest(new UploadedInfo().token("678")));
        expected.add(new InlineKeyboardAttachmentRequest(new InlineKeyboardAttachmentRequestPayload(Arrays.asList(
                Collections.singletonList(button),
                Arrays.asList(button2, button3)
        ))));

        assertThat(attachments, is(expected));
    }

    @Test
    public void test2() {
        Attachment attach1 = new PhotoAttachment(new PhotoAttachmentPayload(1L, "1", "1"));
        Attachment attach2 = new PhotoAttachment(new PhotoAttachmentPayload(2L, "2", "2"));
        Button button = new CallbackButton("1", "1");
        List<AttachmentRequest> result = AttachmentsBuilder.ofAttachments(Arrays.asList(attach1, attach2))
                .with(inlineKeyboard(single(button)))
                .build();

        List<AttachmentRequest> expected = new ArrayList<>();
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token("1")));
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token("2")));
        expected.add(new InlineKeyboardAttachmentRequest(new InlineKeyboardAttachmentRequestPayload(
                Collections.singletonList(Collections.singletonList(button)))));

        assertThat(result, is(expected));
    }
}