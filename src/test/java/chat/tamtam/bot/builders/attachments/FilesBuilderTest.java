package chat.tamtam.bot.builders.attachments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import chat.tamtam.bot.Randoms;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.FileAttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author alexandrchuprin
 */
public class FilesBuilderTest {
    @Test
    public void shouldBuildfromUploadedInfo() {
        String token2 = Randoms.text();
        FilesBuilder builder2 = new FilesBuilder(new UploadedInfo().token(token2));
        List<AttachmentRequest> list2 = builder2.build();
        assertThat(list2, is(Collections.singletonList(new FileAttachmentRequest(new UploadedInfo().token(token2)))));

        String token3 = Randoms.text();
        String token4 = Randoms.text();
        FilesBuilder builder3 = new FilesBuilder(new UploadedInfo().token(token3), new UploadedInfo().token(token4));
        List<AttachmentRequest> list3 = builder3.build();
        assertThat(list3, is(Arrays.asList(
                new FileAttachmentRequest(new UploadedInfo().token(token3)),
                new FileAttachmentRequest(new UploadedInfo().token(token4)))
        ));
    }

    @Test
    public void shouldBuildfromString() {
        String token2 = Randoms.text();
        FilesBuilder builder2 = new FilesBuilder(token2);
        List<AttachmentRequest> list2 = builder2.build();
        assertThat(list2, is(Collections.singletonList(new FileAttachmentRequest(new UploadedInfo().token(token2)))));

        String token3 = Randoms.text();
        String token4 = Randoms.text();
        FilesBuilder builder3 = new FilesBuilder(token3, token4);
        List<AttachmentRequest> list3 = builder3.build();
        assertThat(list3, is(Arrays.asList(
                new FileAttachmentRequest(new UploadedInfo().token(token3)),
                new FileAttachmentRequest(new UploadedInfo().token(token4)))
        ));
    }
}