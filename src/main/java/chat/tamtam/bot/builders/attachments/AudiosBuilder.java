package chat.tamtam.bot.builders.attachments;

import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.AudioAttachmentRequest;
import chat.tamtam.botapi.model.UploadedInfo;

/**
 * @author alexandrchuprin
 */
public class AudiosBuilder extends MediaAttachmentBuilder {
    public AudiosBuilder(UploadedInfo... infos) {
        super(infos);
    }

    public AudiosBuilder(String... tokens) {
        super(tokens);
    }

    @Override
    protected AttachmentRequest toAttachRequest(UploadedInfo uploadedInfo) {
        return new AudioAttachmentRequest(uploadedInfo);
    }
}
