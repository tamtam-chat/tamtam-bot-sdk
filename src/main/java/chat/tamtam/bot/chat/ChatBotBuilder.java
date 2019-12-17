package chat.tamtam.bot.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import chat.tamtam.botapi.model.Message;

/**
 * @author alexandrchuprin
 */
public class ChatBotBuilder {
    private static final Consumer<Message> DO_NOTHING = message -> {
    };

    private final Map<String, CommandHandler> handlers = new ConcurrentHashMap<>();
    private Consumer<Message> defaultHandler;
    private CommandHandler unknownCommandHandler;

    public ChatBotBuilder on(String command, CommandHandler handler) {
        handlers.put(command, handler);
        return this;
    }

    public <T extends CommandHandler & Command> ChatBotBuilder on(T command) {
        handlers.put(command.getKey(), command);
        return this;
    }

    public ChatBotBuilder byDefault(Consumer<Message> defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }

    public ChatBotBuilder onUnknownCommand(CommandHandler unknownCommandHandler) {
        this.unknownCommandHandler = unknownCommandHandler;
        return this;
    }

    public ChatBot build() {
        return new ChatBot(handlers,
                defaultHandler == null ? DO_NOTHING : defaultHandler,
                unknownCommandHandler == null ? CommandHandler.NOOP : unknownCommandHandler);
    }
}
