package chat.tamtam.bot.builders.attachments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import chat.tamtam.bot.Randoms;
import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.AudioAttachmentRequest;
import chat.tamtam.botapi.model.Button;
import chat.tamtam.botapi.model.CallbackButton;
import chat.tamtam.botapi.model.FileAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequestPayload;
import chat.tamtam.botapi.model.PhotoAttachment;
import chat.tamtam.botapi.model.PhotoAttachmentPayload;
import chat.tamtam.botapi.model.PhotoAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachmentRequestPayload;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

import static chat.tamtam.bot.builders.attachments.AttachmentsBuilder.audios;
import static chat.tamtam.bot.builders.attachments.AttachmentsBuilder.files;
import static chat.tamtam.bot.builders.attachments.AttachmentsBuilder.inlineKeyboard;
import static chat.tamtam.bot.builders.attachments.AttachmentsBuilder.videos;
import static chat.tamtam.bot.builders.attachments.InlineKeyboardBuilder.single;
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

        String token1 = Randoms.text();
        String token2 = Randoms.text();
        String token3 = Randoms.text();
        String token4 = Randoms.text();
        String token5 = Randoms.text();
        String token6 = Randoms.text();
        List<AttachmentRequest> attachments = AttachmentsBuilder
                .photos("123", "345")
                .with(PhotosBuilder.byUrls("photoUrl"))
                .with(videos("678"))
                .with(videos(new UploadedInfo().token(token5), new UploadedInfo().token(token6)))
                .with(audios(token1, token2))
                .with(audios(new UploadedInfo().token(token1), new UploadedInfo().token(token2)))
                .with(files(token3, token4))
                .with(files(new UploadedInfo().token(token3), new UploadedInfo().token(token4)))
                .with(inlineKeyboard(single(button).addRow(button2, button3)))
                .build();

        List<AttachmentRequest> expected = new ArrayList<>();
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token("123")));
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token("345")));
        expected.add(new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().url("photoUrl")));

        expected.add(new VideoAttachmentRequest(new UploadedInfo().token("678")));
        expected.add(new VideoAttachmentRequest(new UploadedInfo().token(token5)));
        expected.add(new VideoAttachmentRequest(new UploadedInfo().token(token6)));

        expected.add(new AudioAttachmentRequest(new UploadedInfo().token(token1)));
        expected.add(new AudioAttachmentRequest(new UploadedInfo().token(token2)));
        expected.add(new AudioAttachmentRequest(new UploadedInfo().token(token1)));
        expected.add(new AudioAttachmentRequest(new UploadedInfo().token(token2)));

        expected.add(new FileAttachmentRequest(new UploadedInfo().token(token3)));
        expected.add(new FileAttachmentRequest(new UploadedInfo().token(token4)));
        expected.add(new FileAttachmentRequest(new UploadedInfo().token(token3)));
        expected.add(new FileAttachmentRequest(new UploadedInfo().token(token4)));

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
        List<AttachmentRequest> result = AttachmentsBuilder.copyOf(Arrays.asList(attach1, attach2))
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