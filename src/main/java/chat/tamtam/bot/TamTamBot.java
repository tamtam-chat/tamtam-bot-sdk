package chat.tamtam.bot;

import org.jetbrains.annotations.Nullable;

import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public interface TamTamBot {
    TamTamClient getClient();

    @Nullable
    Object onUpdate(Update update);
}
