package chat.tamtam.bot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated method is a "command handler".
 * Such methods are considered as candidates for auto-detection
 * when their classes added as "handler" to {@link chat.tamtam.bot.TamTamBotBase TamTamBotBase} or its inheritors.
 * An annotated method must have {@link chat.tamtam.botapi.model.Message Message}
 * as the first parameter in the method definition, but amount of parameters can be more than one.
 * <p>
 * There is one more way to define command: using {@link chat.tamtam.bot.commands.Command} and
 * {@link chat.tamtam.bot.commands.CommandHandler}
 *
 * @author alexandrchuprin
 * @see chat.tamtam.bot.TamTamBotBase
 * @see chat.tamtam.botapi.model.Message
 * @see chat.tamtam.bot.commands.Command
 * @see chat.tamtam.bot.commands.CommandHandler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandHandler {

    /**
     * Command name, it mustn't be empty or null.
     * Can start with '/' or without.
     *
     * @return the suggested command name
     */
    String value();

    /**
     * Specifies whether the command's arguments should be parsed.
     */
    boolean parseArgs() default true;
}
