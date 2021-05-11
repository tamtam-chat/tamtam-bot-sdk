package chat.tamtam.bot.builders.attachments;

import java.util.stream.Stream;

import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachmentRequestPayload;

import static java.util.Objects.requireNonNull;

/**
 * @author alexandrchuprin
 */
public class StickerBuilder implements AttachmentsBuilder {
    private final String stickerCode;

    public StickerBuilder(String stickerCode) {
        this.stickerCode = requireNonNull(stickerCode, "stickerCode is null");
    }

    @Override
    public Stream<AttachmentRequest> build() {
        return Stream.of(new StickerAttachmentRequest(new StickerAttachmentRequestPayload(stickerCode)));
    }
}
