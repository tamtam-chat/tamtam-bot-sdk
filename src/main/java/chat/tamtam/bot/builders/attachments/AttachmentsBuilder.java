package chat.tamtam.bot.builders.attachments;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;

import static java.util.Objects.requireNonNull;

/**
 * @author alexandrchuprin
 */
public interface AttachmentsBuilder {
    AttachmentsBuilder EMPTY = Stream::empty;
    AttachmentsBuilder NULL = () -> null;

    Stream<AttachmentRequest> build();

    static AttachmentsBuilder copyOf(List<Attachment> attachments) {
        if (attachments == null) {
            return NULL;
        }
        return requireNonNull(attachments, "attachments").stream()
                .map(AttachmentsBuilder::copyOf)
                .reduce(Concatenating::new)
                .orElse(EMPTY);
    }

    static AttachmentsBuilder copyOf(Attachment attachment) {
        return new CopyBuilder(requireNonNull(attachment, "attachment"));
    }

    static AttachmentsBuilder wrap(List<AttachmentRequest> requests) {
        return requests::stream;
    }

    static AttachmentsBuilder wrap(AttachmentRequest request) {
        return () -> Stream.of(request);
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
        return () -> Stream.of(requireNonNull(keyboardBuilder, "keyboardBuilder").build());
    }

    @Nullable
    default List<AttachmentRequest> getList() {
        Stream<AttachmentRequest> stream = build();
        if (stream == null) {
            return null;
        }

        return stream.collect(Collectors.toList());
    }

    default AttachmentsBuilder with(AttachmentsBuilder anotherBuilder) {
        return new Concatenating(this, requireNonNull(anotherBuilder, "anotherBuilder"));
    }

    default AttachmentsBuilder filtering(Predicate<? super AttachmentRequest> filter) {
        return new Filtering(this, filter);
    }

    default AttachmentsBuilder mapping(Function<AttachmentRequest, AttachmentRequest> mapper) {
        return new Mapping(this, mapper);
    }

    class Concatenating implements AttachmentsBuilder {
        private final AttachmentsBuilder left;
        private final AttachmentsBuilder right;

        Concatenating(AttachmentsBuilder left, AttachmentsBuilder right) {
            this.left = requireNonNull(left, "left");
            this.right = requireNonNull(right, "right");
        }

        @Override
        public Stream<AttachmentRequest> build() {
            return Stream.concat(left.build(), right.build());
        }
    }

    class Filtering implements AttachmentsBuilder {
        private final AttachmentsBuilder upstream;
        private final Predicate<? super AttachmentRequest> filter;

        public Filtering(AttachmentsBuilder upstream, Predicate<? super AttachmentRequest> filter) {
            this.upstream = upstream;
            this.filter = filter;
        }

        @Override
        public Stream<AttachmentRequest> build() {
            return upstream.build().filter(filter);
        }
    }

    class Mapping implements AttachmentsBuilder {
        private final AttachmentsBuilder upstream;
        private final Function<AttachmentRequest, AttachmentRequest> mapper;

        public Mapping(AttachmentsBuilder upstream, Function<AttachmentRequest, AttachmentRequest> mapper) {
            this.upstream = upstream;
            this.mapper = mapper;
        }

        @Override
        public Stream<AttachmentRequest> build() {
            return upstream.build().map(mapper);
        }

        public static <T extends AttachmentRequest> Function<AttachmentRequest, AttachmentRequest> byType(
                Class<T> clazz, Function<T, AttachmentRequest> typedMapper) {

            return attachmentRequest -> {
                if (attachmentRequest.getClass() == clazz) {
                    return typedMapper.apply((T) attachmentRequest);
                }

                return attachmentRequest;
            };
        }
    }
}
