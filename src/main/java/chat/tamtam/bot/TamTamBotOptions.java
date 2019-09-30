package chat.tamtam.bot;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

/**
 * @author alexandrchuprin
 */
public class TamTamBotOptions {
    private final Set<String> updateTypes;

    public TamTamBotOptions(@Nullable Set<String> updateTypes) {
        this.updateTypes = updateTypes;
    }

    public Set<String> getUpdateTypes() {
        return updateTypes;
    }
}
