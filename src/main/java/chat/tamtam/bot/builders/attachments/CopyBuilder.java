package chat.tamtam.bot.builders.attachments;

import java.util.Collections;
import java.util.List;

import chat.tamtam.bot.builders.AttachmentsBuilder;
import chat.tamtam.bot.builders.InlineKeyboardBuilder;
import chat.tamtam.botapi.model.Attachment;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.AudioAttachment;
import chat.tamtam.botapi.model.AudioAttachmentRequest;
import chat.tamtam.botapi.model.ContactAttachment;
import chat.tamtam.botapi.model.ContactAttachmentRequest;
import chat.tamtam.botapi.model.ContactAttachmentRequestPayload;
import chat.tamtam.botapi.model.FileAttachment;
import chat.tamtam.botapi.model.FileAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachment;
import chat.tamtam.botapi.model.LocationAttachment;
import chat.tamtam.botapi.model.LocationAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachment;
import chat.tamtam.botapi.model.PhotoAttachmentRequest;
import chat.tamtam.botapi.model.PhotoAttachmentRequestPayload;
import chat.tamtam.botapi.model.ShareAttachment;
import chat.tamtam.botapi.model.ShareAttachmentPayload;
import chat.tamtam.botapi.model.ShareAttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachment;
import chat.tamtam.botapi.model.StickerAttachmentRequest;
import chat.tamtam.botapi.model.StickerAttachmentRequestPayload;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.VideoAttachment;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

/**
 * Creates {@link AttachmentRequest} from existing {@link Attachment}
 *
 * @author alexandrchuprin
 */
public class CopyBuilder implements AttachmentsBuilder, Attachment.Visitor {
    private final Attachment attachment;
    private List<AttachmentRequest> result;

    public CopyBuilder(Attachment attachment) {
        this.attachment = attachment;
    }

    @Override
    public void visit(PhotoAttachment model) {
        String token = model.getPayload().getToken();
        result = Collections.singletonList(
                new PhotoAttachmentRequest(new PhotoAttachmentRequestPayload().token(token)));
    }

    @Override
    public void visit(VideoAttachment model) {
        result = Collections.singletonList(
                new VideoAttachmentRequest(new UploadedInfo().token(model.getPayload().getToken())));
    }

    @Override
    public void visit(AudioAttachment model) {
        result = Collections.singletonList(
                new AudioAttachmentRequest(new UploadedInfo().token(model.getPayload().getToken())));
    }

    @Override
    public void visit(FileAttachment model) {
        result = Collections.singletonList(
                new FileAttachmentRequest(new UploadedInfo().token(model.getPayload().getToken())));
    }

    @Override
    public void visit(StickerAttachment model) {
        result = Collections.singletonList(
                new StickerAttachmentRequest(new StickerAttachmentRequestPayload(model.getPayload().getCode())));
    }

    @Override
    public void visit(ContactAttachment model) {
        // todo: make attach convertible to request
        result = Collections.singletonList(new ContactAttachmentRequest(
                new ContactAttachmentRequestPayload(model.getPayload().getTamInfo().getName())));
    }

    @Override
    public void visit(InlineKeyboardAttachment model) {
        result = Collections.singletonList(InlineKeyboardBuilder.layout(model.getPayload().getButtons()).build());
    }

    @Override
    public void visit(ShareAttachment model) {
        result = Collections.singletonList(
                new ShareAttachmentRequest(new ShareAttachmentPayload().token(model.getPayload().getToken())));
    }

    @Override
    public void visit(LocationAttachment model) {
        result = Collections.singletonList(
                new LocationAttachmentRequest(model.getLatitude(), model.getLongitude()));
    }

    @Override
    public void visitDefault(Attachment model) {
        throw new UnsupportedOperationException("Attachment is unsupported: " + model);
    }

    @Override
    public List<AttachmentRequest> build() {
        attachment.visit(this);
        return result;
    }
}
