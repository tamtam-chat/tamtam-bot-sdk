package chat.tamtam.bot.builders;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import chat.tamtam.botapi.model.Button;
import chat.tamtam.botapi.model.CallbackButton;
import chat.tamtam.botapi.model.InlineKeyboardAttachmentRequest;

import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author alexandrchuprin
 */
public class InlineKeyboardBuilderTest {
    @Test
    public void test1() {
        Button[] buttons = IntStream.range(0, 10).mapToObj(InlineKeyboardBuilderTest::button).toArray(Button[]::new);
        InlineKeyboardAttachmentRequest ar = InlineKeyboardBuilder.empty()
                .addRow(buttons[0], buttons[1])
                .addRow(buttons[2], buttons[3])
                .addRow()
                .addButton(buttons[4])
                .addRow(buttons[5], buttons[6])
                .addButton(buttons[7])
                .addRow()
                .addButton(buttons[8])
                .build();

        List<List<Button>> result = ar.getPayload().getButtons();
        // first row
        assertThat(result.get(0).get(0), is(buttons[0]));
        assertThat(result.get(0).get(1), is(buttons[1]));
        // second row
        assertThat(result.get(1).get(0), is(buttons[2]));
        assertThat(result.get(1).get(1), is(buttons[3]));
        // third row
        assertThat(result.get(2).get(0), is(buttons[4]));
        // fourth row
        assertThat(result.get(3).get(0), is(buttons[5]));
        assertThat(result.get(3).get(1), is(buttons[6]));
        assertThat(result.get(3).get(2), is(buttons[7]));
        // fifth row
        assertThat(result.get(4).get(0), is(buttons[8]));
    }

    @Test
    public void test2() {
        CallbackButton button = button(0);
        InlineKeyboardAttachmentRequest attach = InlineKeyboardBuilder.single(button).build();
        assertThat(attach.getPayload().getButtons(), is(singletonList(singletonList(button))));
    }

    @Test
    public void test3() {
        CallbackButton button = button(0);
        Button button2 = button(1);
        Button button3 = button(3);
        InlineKeyboardAttachmentRequest attach = InlineKeyboardBuilder.singleRow(button, button2, button3).build();
        assertThat(attach.getPayload().getButtons(), is(singletonList(Arrays.asList(button, button2, button3))));
    }

    @Test
    public void test4() {
        CallbackButton button = button(0);
        Button button2 = button(1);
        Button button3 = button(3);
        InlineKeyboardAttachmentRequest attach = InlineKeyboardBuilder.singleColumn(button, button2, button3).build();
        assertThat(attach.getPayload().getButtons(), is(Arrays.asList(
                singletonList(button),
                singletonList(button2),
                singletonList(button3)
        )));
    }

    @Test
    public void test5() {
        CallbackButton button = button(0);
        Button button2 = button(1);
        Button button3 = button(3);
        InlineKeyboardAttachmentRequest attach = InlineKeyboardBuilder.layout(
                Arrays.asList(singletonList(button), Arrays.asList(button2, button3))
        ).build();

        assertThat(attach.getPayload().getButtons(), is(Arrays.asList(
                singletonList(button),
                Arrays.asList(button2, button3)
        )));
    }

    private static CallbackButton button(int i) {
        return new CallbackButton("payload" + i, "text" + i);
    }
}