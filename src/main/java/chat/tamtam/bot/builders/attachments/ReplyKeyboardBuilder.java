package chat.tamtam.bot.builders.attachments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chat.tamtam.botapi.model.ReplyButton;
import chat.tamtam.botapi.model.ReplyKeyboardAttachmentRequest;

/**
 * @author alexandrchuprin
 */
public class ReplyKeyboardBuilder {
    final List<List<ReplyButton>> result;

    protected ReplyKeyboardBuilder() {
        this.result = new ArrayList<>();
    }

    protected ReplyKeyboardBuilder(List<List<ReplyButton>> initial) {
        this.result = new ArrayList<>(initial.size());
        for (List<ReplyButton> row : initial) {
            this.result.add(new ArrayList<>(row));
        }
    }

    public static ReplyKeyboardBuilder empty() {
        return new ReplyKeyboardBuilder();
    }

    public static ReplyKeyboardBuilder single(ReplyButton button) {
        return new ReplyKeyboardBuilder(Collections.singletonList(Collections.singletonList(button)));
    }

    public static ReplyKeyboardBuilder singleRow(ReplyButton... buttons) {
        return new ReplyKeyboardBuilder(Collections.singletonList(Arrays.asList(buttons)));
    }

    public static ReplyKeyboardBuilder singleColumn(ReplyButton... buttons) {
        return new ReplyKeyboardBuilder(
                Stream.of(buttons).map(Collections::singletonList).collect(Collectors.toList()));
    }

    public static ReplyKeyboardBuilder layout(List<List<ReplyButton>> buttons) {
        return new ReplyKeyboardBuilder(buttons);
    }

    public RowBuilder addRow(ReplyButton... buttons) {
        return new RowBuilder(buttons);
    }

    public ReplyKeyboardAttachmentRequest build() {
        return new ReplyKeyboardAttachmentRequest(result);
    }

    public class RowBuilder extends ReplyKeyboardBuilder {
        final List<ReplyButton> row;

        private RowBuilder(ReplyButton... buttons) {
            super(ReplyKeyboardBuilder.this.result);
            row = new ArrayList<>();
            if (buttons != null) {
                row.addAll(Arrays.asList(buttons));
            }

            result.add(row);
        }

        public RowBuilder addButton(ReplyButton button) {
            row.add(button);
            return this;
        }
    }
}
