package chat.tamtam.bot.builders.attachments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import chat.tamtam.bot.Randoms;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.AudioAttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author alexandrchuprin
 */
public class AudiosBuilderTest {
    @Test
    public void shouldBuildfromUploadedInfo() {
        String token2 = Randoms.text();
        AudiosBuilder builder2 = new AudiosBuilder(new UploadedInfo().token(token2));
        List<AttachmentRequest> list2 = builder2.getList();
        assertThat(list2, is(Collections.singletonList(new AudioAttachmentRequest(new UploadedInfo().token(token2)))));

        String token3 = Randoms.text();
        String token4 = Randoms.text();
        AudiosBuilder builder3 = new AudiosBuilder(new UploadedInfo().token(token3), new UploadedInfo().token(token4));
        List<AttachmentRequest> list3 = builder3.getList();
        assertThat(list3, is(Arrays.asList(
                new AudioAttachmentRequest(new UploadedInfo().token(token3)),
                new AudioAttachmentRequest(new UploadedInfo().token(token4)))
        ));
    }

    @Test
    public void shouldBuildfromString() {
        String token2 = Randoms.text();
        AudiosBuilder builder2 = new AudiosBuilder(token2);
        List<AttachmentRequest> list2 = builder2.getList();
        assertThat(list2, is(Collections.singletonList(new AudioAttachmentRequest(new UploadedInfo().token(token2)))));

        String token3 = Randoms.text();
        String token4 = Randoms.text();
        AudiosBuilder builder3 = new AudiosBuilder(token3, token4);
        List<AttachmentRequest> list3 = builder3.getList();
        assertThat(list3, is(Arrays.asList(
                new AudioAttachmentRequest(new UploadedInfo().token(token3)),
                new AudioAttachmentRequest(new UploadedInfo().token(token4)))
        ));
    }
}