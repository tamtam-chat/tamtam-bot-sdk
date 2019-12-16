package chat.tamtam.bot.builders.attachments;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import chat.tamtam.bot.builders.AttachmentsBuilder;
import chat.tamtam.botapi.model.PhotoAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachmentRequestPayload;

/**
 * @author alexandrchuprin
 */
public class PhotosBuilder {
    public static AttachmentsBuilder byTokens(String... tokens) {
        return () -> Arrays.stream(Objects.requireNonNull(tokens, "tokens"))
                .map(token -> new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token(token)))
                .collect(Collectors.toList());
    }

    public static AttachmentsBuilder byUrls(String... urls) {
        return () -> Arrays.stream(urls)
                .map(url -> new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().url(url)))
                .collect(Collectors.toList());
    }
}
