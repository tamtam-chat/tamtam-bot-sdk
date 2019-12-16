package chat.tamtam.bot.builders.attachments;

import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.FileAttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;

/**
 * @author alexandrchuprin
 */
public class FilesBuilder extends MediaAttachmentBuilder {
    public FilesBuilder(UploadedInfo... uploadedInfos) {
        super(uploadedInfos);
    }

    public FilesBuilder(String... tokens) {
        super(tokens);
    }

    @Override
    protected AttachmentRequest toAttachRequest(UploadedInfo uploadedInfo) {
        return new FileAttachmentRequest(uploadedInfo);
    }
}
