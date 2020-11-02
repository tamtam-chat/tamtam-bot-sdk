package chat.tamtam.bot.builders.attachments;

import java.util.Objects;
import java.util.stream.Stream;

import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.AudioAttachment;
import chat.tamtam.botapi.model.AudioAttachmentRequest;
import chat.tamtam.botapi.model.ContactAttachment;
import chat.tamtam.botapi.model.ContactAttachmentRequest;
import chat.tamtam.botapi.model.ContactAttachmentRequestPayload;
import chat.tamtam.botapi.model.DataAttachment;
import chat.tamtam.botapi.model.FileAttachment;
import chat.tamtam.botapi.model.FileAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachment;
import chat.tamtam.botapi.model.LocationAttachment;
import chat.tamtam.botapi.model.LocationAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachment;
import chat.tamtam.botapi.model.PhotoAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachmentRequestPayload;
import chat.tamtam.botapi.model.ReplyKeyboardAttachment;
import chat.tamtam.botapi.model.ShareAttachment;
import chat.tamtam.botapi.model.ShareAttachmentPayload;
import chat.tamtam.botapi.model.ShareAttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachment;
import chat.tamtam.botapi.model.StickerAttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachmentRequestPayload;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.User;
import chat.tamtam.botapi.model.VideoAttachment;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

/**
 * Creates {@link AttachmentRequest} from existing {@link Attachment}
 *
 * @author alexandrchuprin
 */
public class CopyBuilder implements AttachmentsBuilder, Attachment.Mapper<AttachmentRequest> {
    private final Attachment attachment;

    public CopyBuilder(Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public AttachmentRequest map(PhotoAttachment model) {
        String token = model.getPayload().getToken();
        return new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token(token));
    }

    @Override
    public AttachmentRequest map(VideoAttachment model) {
        return new VideoAttachmentRequest(new UploadedInfo().token(model.getPayload().getToken()));
    }

    @Override
    public AttachmentRequest map(AudioAttachment model) {
        return new AudioAttachmentRequest(new UploadedInfo().token(model.getPayload().getToken()));
    }

    @Override
    public AttachmentRequest map(FileAttachment model) {
        return new FileAttachmentRequest(new UploadedInfo().token(model.getPayload().getToken()));
    }

    @Override
    public AttachmentRequest map(StickerAttachment model) {
        return new StickerAttachmentRequest(new StickerAttachmentRequestPayload(model.getPayload().getCode()));
    }

    @Override
    public AttachmentRequest map(ContactAttachment model) {
        User tamInfo = Objects.requireNonNull(model.getPayload().getTamInfo(), "tamInfo is required");
        return new ContactAttachmentRequest(
                new ContactAttachmentRequestPayload(tamInfo.getName()).contactId(tamInfo.getUserId()));
    }

    @Override
    public AttachmentRequest map(InlineKeyboardAttachment model) {
        return InlineKeyboardBuilder.layout(model.getPayload().getButtons()).build();
    }

    @Override
    public AttachmentRequest map(ReplyKeyboardAttachment replyKeyboardAttachment) {
        return ReplyKeyboardBuilder.layout(replyKeyboardAttachment.getButtons()).build();
    }

    @Override
    public AttachmentRequest map(ShareAttachment model) {
        return new ShareAttachmentRequest(new ShareAttachmentPayload()
                .token(model.getPayload().getToken())
                .url(model.getPayload().getUrl()));
    }

    @Override
    public AttachmentRequest map(LocationAttachment model) {
        return new LocationAttachmentRequest(model.getLatitude(), model.getLongitude());
    }

    @Override
    public AttachmentRequest map(DataAttachment dataAttachment) {
        // not supported
        return null;
    }

    @Override
    public AttachmentRequest mapDefault(Attachment model) {
        return null;
    }

    @Override
    public Stream<AttachmentRequest> build() {
        AttachmentRequest request = attachment.map(this);
        return request == null ? Stream.empty() : Stream.of(request);
    }
}
