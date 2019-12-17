package chat.tamtam.bot.chat;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

/**
 * @author alexandrchuprin
 */
public class CommandLineParser {
    private static final String[] NO_ARGS = new String[0];

    @Nullable
    public static CommandLine tryParse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        // all commands should start with `/`
        if (line.charAt(0) != '/') {
            return null;
        }

        String trimmedLine = line.substring(1).trim();
        if (trimmedLine.isEmpty()) {
            return null;
        }

        int firstSpace = trimmedLine.indexOf(' ');
        if (firstSpace == -1) {
            return new CommandLine(trimmedLine, NO_ARGS);
        }

        String commandKey = trimmedLine.substring(0, firstSpace);
        String[] args = parseArgs(trimmedLine.substring(firstSpace + 1));

        return new CommandLine(commandKey, args);
    }

    private static String[] parseArgs(String argsString) {
        List<String> args = new ArrayList<>();
        StringBuilder argBuilder = new StringBuilder();
        boolean escaped = false;
        boolean enquoted = false;
        for (char c : argsString.toCharArray()) {
            if (c == 32) { // space
                if (!enquoted) {
                    args.add(argBuilder.toString());
                    argBuilder = new StringBuilder();
                    continue;
                }
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"') {
                if (escaped) {
                    argBuilder.append(c);
                    escaped = false;
                    continue;
                }

                enquoted = !enquoted;
                continue;
            }

            argBuilder.append(c);
        }

        args.add(argBuilder.toString());

        return args.toArray(new String[0]);
    }
}
