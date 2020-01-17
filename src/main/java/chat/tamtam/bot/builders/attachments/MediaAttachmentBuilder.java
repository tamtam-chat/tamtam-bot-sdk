package chat.tamtam.bot.builders.attachments;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;

/**
 * @author alexandrchuprin
 */
public abstract class MediaAttachmentBuilder implements AttachmentsBuilder {
    private final UploadedInfo[] uploadedInfos;

    protected MediaAttachmentBuilder(UploadedInfo... uploadedInfos) {
        this.uploadedInfos = uploadedInfos;
    }

    public MediaAttachmentBuilder(String... tokens) {
        this.uploadedInfos = Arrays.stream(Objects.requireNonNull(tokens, "tokens"))
                .map(t -> new UploadedInfo().token(t))
                .toArray(UploadedInfo[]::new);
    }

    protected abstract AttachmentRequest toAttachRequest(UploadedInfo uploadedInfo);

    @Override
    public List<AttachmentRequest> build() {
        return Arrays.stream(uploadedInfos)
                .map(this::toAttachRequest)
                .collect(Collectors.toList());
    }
}
