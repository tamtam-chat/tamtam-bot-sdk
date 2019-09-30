package chat.tamtam.bot;

import chat.tamtam.botapi.model.Update;

/**
 * @author alexandrchuprin
 */
public interface TamTamBot {
    void onUpdate(Update update);
}
