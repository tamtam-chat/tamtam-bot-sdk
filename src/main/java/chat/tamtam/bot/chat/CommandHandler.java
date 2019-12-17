package chat.tamtam.bot.chat;

import chat.tamtam.botapi.model.Message;

/**
 * @author alexandrchuprin
 */
public interface CommandHandler {
    CommandHandler NOOP = (message, commandLine) -> {

    };

    void execute(Message message, CommandLine commandLine);
}
