package chat.tamtam.bot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated method handles incoming updates.
 * Such methods are considered as candidates for auto-detection
 * when its containing class added as "handler" to {@link chat.tamtam.bot.TamTamBotBase TamTamBotBase} or its inheritors.
 * An annotated method must have one parameter with inherited type {@link chat.tamtam.botapi.model.Update Update}
 * in the method definition.
 *
 * @author alexandrchuprin
 * @see chat.tamtam.bot.TamTamBotBase
 * @see chat.tamtam.botapi.model.Update
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UpdateHandler {
}
