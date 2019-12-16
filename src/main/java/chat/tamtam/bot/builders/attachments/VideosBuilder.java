package chat.tamtam.bot.builders.attachments;

import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;
import chat.tamtam.botapi.model.VideoAttachmentRequest;

/**
 * @author alexandrchuprin
 */
public class VideosBuilder extends MediaAttachmentBuilder {
    public VideosBuilder(UploadedInfo... uploadedInfos) {
        super(uploadedInfos);
    }

    public VideosBuilder(String... tokens) {
        super(tokens);
    }

    @Override
    protected AttachmentRequest toAttachRequest(UploadedInfo uploadedInfo) {
        return new VideoAttachmentRequest(uploadedInfo);
    }

}
