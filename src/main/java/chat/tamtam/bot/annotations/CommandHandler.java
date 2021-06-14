package chat.tamtam.bot.annotations;

import chat.tamtam.botapi.model.MessageCreatedUpdate;

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
 *
 * @author alexandrchuprin
 * @see chat.tamtam.bot.TamTamBotBase
 * @see chat.tamtam.botapi.model.Message
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandHandler {

    /**
     * The name of the command, it mustn't be empty or null.
     * Command's name can start with '/' or without.
     * @return the suggested command name
     */
    String value();

    /**
     * Specifies whether the command's arguments should be parsed.
     *
     * @see chat.tamtam.bot.TamTamBotBase#tryHandleCommand(MessageCreatedUpdate) 
     */
    boolean parseArgs() default true;
}
