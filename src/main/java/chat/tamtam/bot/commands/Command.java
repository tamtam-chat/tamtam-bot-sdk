package chat.tamtam.bot.commands;

/**
 * @author alexandrchuprin
 */
public interface Command {
    /**
     * @return command name without '/'
     */
    String getKey();
}
