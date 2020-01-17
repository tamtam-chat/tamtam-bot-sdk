package chat.tamtam.bot.commands;

import chat.tamtam.botapi.model.Message;

/**
 * @author alexandrchuprin
 */
public interface CommandHandler {
    CommandHandler NOOP = (message, commandLine) -> {

    };

    void execute(Message message, CommandLine commandLine);
}
