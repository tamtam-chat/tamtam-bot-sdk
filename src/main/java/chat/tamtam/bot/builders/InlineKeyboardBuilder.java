package chat.tamtam.bot.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import chat.tamtam.botapi.model.Button;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequest;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequestPayload;

/**
 * @author alexandrchuprin
 */
public class InlineKeyboardBuilder {
    final List<List<Button>> result;

    protected InlineKeyboardBuilder() {
        this.result = new ArrayList<>();
    }

    protected InlineKeyboardBuilder(List<List<Button>> initial) {
        this.result = new ArrayList<>(initial.size());
        for (List<Button> row : initial) {
            this.result.add(new ArrayList<>(row));
        }
    }

    public static InlineKeyboardBuilder empty() {
        return new InlineKeyboardBuilder();
    }

    public static InlineKeyboardBuilder single(Button button) {
        return new InlineKeyboardBuilder(Collections.singletonList(Collections.singletonList(button)));
    }

    public static InlineKeyboardBuilder singleRow(Button... buttons) {
        return new InlineKeyboardBuilder(Collections.singletonList(Arrays.asList(buttons)));
    }

    public static InlineKeyboardBuilder singleColumn(Button... buttons) {
        return new InlineKeyboardBuilder(
                Stream.of(buttons).map(Collections::singletonList).collect(Collectors.toList()));
    }

    public static InlineKeyboardBuilder layout(List<List<Button>> buttons) {
        return new InlineKeyboardBuilder(buttons);
    }

    public RowBuilder addRow(Button... buttons) {
        return new RowBuilder(buttons);
    }

    public InlineKeyboardAttachmentRequest build() {
        InlineKeyboardAttachmentRequestPayload payload = new InlineKeyboardAttachmentRequestPayload(result);
        return new InlineKeyboardAttachmentRequest(payload);
    }

    public class RowBuilder extends InlineKeyboardBuilder {
        final List<Button> row;

        private RowBuilder(Button... buttons) {
            super(InlineKeyboardBuilder.this.result);
            row = new ArrayList<>();
            if (buttons != null) {
                row.addAll(Arrays.asList(buttons));
            }

            result.add(row);
        }

        public RowBuilder addButton(Button button) {
            row.add(button);
            return this;
        }
    }
}
