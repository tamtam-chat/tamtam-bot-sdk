package chat.tamtam.bot.commands;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.Nullable;

/**
 * Describes a command with not parsed arguments.
 *
 * @author alexandrchuprin
 */
public class RawCommandLine {
    private final String key;
    private final String tail;

    public RawCommandLine(String key, @Nullable String tail) {
        this.key = Objects.requireNonNull(key, "key").toLowerCase();
        this.tail = Objects.requireNonNull(tail, "tail");
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public String getTail() {
        return tail;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("key", key)
                .append("tail", tail)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RawCommandLine)) return false;

        RawCommandLine that = (RawCommandLine) o;

        return new EqualsBuilder()
                .append(key, that.key)
                .append(tail, that.tail)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(key)
                .append(tail)
                .toHashCode();
    }
}
