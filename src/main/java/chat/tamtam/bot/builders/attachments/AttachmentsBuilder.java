package chat.tamtam.bot.builders.attachments;

import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author alexandrchuprin
 */
public interface AttachmentsBuilder {
    AttachmentsBuilder EMPTY = Collections::emptyList;
    AttachmentsBuilder NULL = () -> null;

    List<AttachmentRequest> build();

    static AttachmentsBuilder copyOf(List<Attachment> attachments) {
        if (attachments == null) {
            return NULL;
        }
        return requireNonNull(attachments, "attachments").stream()
                .map(AttachmentsBuilder::copyOf)
                .reduce(Concat::new)
                .orElse(EMPTY);
    }

    static AttachmentsBuilder copyOf(Attachment attachment) {
        return new CopyBuilder(requireNonNull(attachment, "attachment"));
    }

    static AttachmentsBuilder wrap(List<AttachmentRequest> requests) {
        return () -> requests;
    }

    static AttachmentsBuilder wrap(AttachmentRequest request) {
        return () -> Collections.singletonList(request);
    }

    static AttachmentsBuilder photos(String... tokens) {
        return PhotosBuilder.byTokens(tokens);
    }

    static AttachmentsBuilder videos(String... tokens) {
        return new VideosBuilder(tokens);
    }

    static AttachmentsBuilder videos(UploadedInfo... uploadedInfos) {
        return new VideosBuilder(uploadedInfos);
    }

    static AttachmentsBuilder audios(String... tokens) {
        return new AudiosBuilder(tokens);
    }

    static AttachmentsBuilder audios(UploadedInfo... uploadedInfos) {
        return new AudiosBuilder(uploadedInfos);
    }

    static AttachmentsBuilder files(String... tokens) {
        return new FilesBuilder(tokens);
    }

    static AttachmentsBuilder files(UploadedInfo... uploadedInfos) {
        return new FilesBuilder(uploadedInfos);
    }

    static AttachmentsBuilder inlineKeyboard(InlineKeyboardBuilder keyboardBuilder) {
        return () -> Collections.singletonList(requireNonNull(keyboardBuilder, "keyboardBuilder").build());
    }

    default AttachmentsBuilder with(AttachmentsBuilder anotherBuilder) {
        return new Concat(this, requireNonNull(anotherBuilder, "anotherBuilder"));
    }

    class Concat implements AttachmentsBuilder {
        private final AttachmentsBuilder left;
        private final AttachmentsBuilder right;

        Concat(AttachmentsBuilder left, AttachmentsBuilder right) {
            this.left = requireNonNull(left, "left");
            this.right = requireNonNull(right, "right");
        }

        @Override
        public List<AttachmentRequest> build() {
            return Stream.concat(left.build().stream(), right.build().stream()).collect(Collectors.toList());
        }
    }
}
