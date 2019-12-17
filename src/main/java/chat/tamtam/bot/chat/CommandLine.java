package chat.tamtam.bot.chat;

import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author alexandrchuprin
 */
public class CommandLine {
    private final String key;
    private final String[] args;

    CommandLine(String key, String[] args) {
        this.key = Objects.requireNonNull(key, "key");
        this.args = Objects.requireNonNull(args, "args");
    }

    public String getKey() {
        return key;
    }

    public String[] getArgs() {
        return args;
    }

    public String getArg(int index) {
        return getArg(index, Function.identity());
    }

    public <T> T getArg(int index, Function<String, T> converter) {
        if (index < 0 || index >= args.length) {
            throw new ArrayIndexOutOfBoundsException("No argument with index " + index);
        }

        return converter.apply(args[index]);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("key", key)
                .append("args", args)
                .toString();
    }
}
