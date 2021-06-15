package chat.tamtam.bot.commands;

import chat.tamtam.botapi.model.Message;

/**
 * @author alexandrchuprin
 */
public interface CommandHandler {
    CommandHandler NOOP = (message, commandLine) -> {

    };

    /**
     * Method to handle incoming bot command.
     * @param message message from {@link chat.tamtam.botapi.model.MessageCreatedUpdate MessageCreatedUpdate}
     * @param commandLine parsed command
     */
    void execute(Message message, CommandLine commandLine);
}
