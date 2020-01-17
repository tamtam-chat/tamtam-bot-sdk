package chat.tamtam.bot.builders.attachments;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import chat.tamtam.bot.Randoms;
import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.AudioAttachment;
import chat.tamtam.botapi.model.AudioAttachmentRequest;
import chat.tamtam.botapi.model.Button;
import chat.tamtam.botapi.model.ContactAttachment;
import chat.tamtam.botapi.model.ContactAttachmentPayload;
import chat.tamtam.botapi.model.ContactAttachmentRequest;
import chat.tamtam.botapi.model.ContactAttachmentRequestPayload;
import chat.tamtam.botapi.model.FileAttachment;
import chat.tamtam.botapi.model.FileAttachmentPayload;
import chat.tamtam.botapi.model.FileAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachment;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequestPayload;
import chat.tamtam.botapi.model.Keyboard;
import chat.tamtam.botapi.model.LinkButton;
import chat.tamtam.botapi.model.LocationAttachment;
import chat.tamtam.botapi.model.LocationAttachmentRequest;
import chat.tamtam.botapi.model.MediaAttachmentPayload;
import chat.tamtam.botapi.model.PhotoAttachment;
import chat.tamtam.botapi.model.PhotoAttachmentPayload;
import chat.tamtam.botapi.model.PhotoAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachmentRequestPayload;
import chat.tamtam.botapi.model.ShareAttachment;
import chat.tamtam.botapi.model.ShareAttachmentPayload;
import chat.tamtam.botapi.model.ShareAttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachment;
import chat.tamtam.botapi.model.StickerAttachmentPayload;
import chat.tamtam.botapi.model.StickerAttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachmentRequestPayload;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.User;
import chat.tamtam.botapi.model.VideoAttachment;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author alexandrchuprin
 */
public class CopyBuilderTest {
    @Test
    public void test0() {
        // unknown attachment
        List<AttachmentRequest> result = new CopyBuilder(new Attachment()).build();
        assertThat(result, is(Collections.emptyList()));
    }

    @Test
    public void test1() {
        String token = Randoms.text();
        List<AttachmentRequest> result = new CopyBuilder(
                new PhotoAttachment(new PhotoAttachmentPayload(123L, token, "url"))).build();

        assertThat(result, is(Collections.singletonList(
                new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token(token)))));
    }

    @Test
    public void test2() {
        String token = Randoms.text();
        List<AttachmentRequest> result = new CopyBuilder(
                new VideoAttachment(new MediaAttachmentPayload(token, "url"))).build();

        assertThat(result, is(Collections.singletonList(
                new VideoAttachmentRequest(new UploadedInfo().token(token)))));
    }

    @Test
    public void test3() {
        String token = Randoms.text();
        List<AttachmentRequest> result = new CopyBuilder(
                new AudioAttachment(new MediaAttachmentPayload(token, "url"))).build();

        assertThat(result, is(Collections.singletonList(
                new AudioAttachmentRequest(new UploadedInfo().token(token)))));
    }

    @Test
    public void test4() {
        String token = Randoms.text();
        List<AttachmentRequest> result = new CopyBuilder(
                new FileAttachment(new FileAttachmentPayload(token, "url"), "filename", 1L)).build();

        assertThat(result, is(Collections.singletonList(
                new FileAttachmentRequest(new UploadedInfo().token(token)))));
    }

    @Test
    public void test5() {
        String code = Randoms.text();
        List<AttachmentRequest> result = new CopyBuilder(
                new StickerAttachment(new StickerAttachmentPayload(code, "url"), 1, 2)).build();

        assertThat(result, is(Collections.singletonList(
                new StickerAttachmentRequest(new StickerAttachmentRequestPayload(code)))));
    }

    @Test
    public void test6() {
        User user = Randoms.randomUser();
        List<AttachmentRequest> result = new CopyBuilder(
                new ContactAttachment(new ContactAttachmentPayload().tamInfo(user))).build();

        assertThat(result, is(Collections.singletonList(
                new ContactAttachmentRequest(
                        new ContactAttachmentRequestPayload(user.getName()).contactId(user.getUserId())))));
    }

    @Test
    public void test7() {
        List<List<Button>> buttons = Collections.singletonList(
                Collections.singletonList(new LinkButton(Randoms.text(), Randoms.text())));

        Keyboard keyboard = new Keyboard(buttons);
        List<AttachmentRequest> result = new CopyBuilder(new InlineKeyboardAttachment(keyboard)).build();

        assertThat(result, is(Collections.singletonList(
                new InlineKeyboardAttachmentRequest(new InlineKeyboardAttachmentRequestPayload(buttons)))));
    }

    @Test
    public void test8() {
        String url = Randoms.text();
        String token = Randoms.text();
        ShareAttachmentPayload payload = new ShareAttachmentPayload().url(url).token(token);
        List<AttachmentRequest> result = new CopyBuilder(new ShareAttachment(payload)).build();
        assertThat(result, is(Collections.singletonList(new ShareAttachmentRequest(payload))));
    }

    @Test
    public void test9() {
        double latitude = Randoms.randomDouble();
        double longitude = Randoms.randomDouble();
        List<AttachmentRequest> result = new CopyBuilder(new LocationAttachment(latitude, longitude)).build();
        assertThat(result, is(Collections.singletonList(new LocationAttachmentRequest(latitude, longitude))));
    }
}