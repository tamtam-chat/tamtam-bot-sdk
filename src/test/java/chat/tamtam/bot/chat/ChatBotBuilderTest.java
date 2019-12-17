package chat.tamtam.bot.chat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import static chat.tamtam.bot.Mocks.message;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author alexandrchuprin
 */
public class ChatBotBuilderTest {
    @Test
    public void test1() {
        AtomicBoolean start = new AtomicBoolean();
        AtomicBoolean help = new AtomicBoolean();
        AtomicBoolean unknown = new AtomicBoolean();
        AtomicBoolean byDefault = new AtomicBoolean();
        ChatBotBuilder builder = new ChatBotBuilder()
                .on("start", (message, commandLine) -> {
                    if (!start.compareAndSet(false, true)) {
                        fail();
                    }
                })
                .on("help", (message, commandLine) -> {
                    if (!help.compareAndSet(false, true)) {
                        fail();
                    }
                })
                .onUnknownCommand((message, commandLine) -> {
                    if (!unknown.compareAndSet(false, true)) {
                        fail();
                    }
                })
                .byDefault(message -> {
                    if (!byDefault.compareAndSet(false, true)) {
                        fail();
                    }
                });
        ;

        ChatBot chatBot = builder.build();
        chatBot.replyOn(message("/start"));
        chatBot.replyOn(message("/help"));
        chatBot.replyOn(message("/unknown"));
        chatBot.replyOn(message("notcommand"));

        assertThat(start.get(), is(true));
        assertThat(help.get(), is(true));
        assertThat(unknown.get(), is(true));
        assertThat(byDefault.get(), is(true));
    }

    @Test
    public void test2() {
        AtomicBoolean start = new AtomicBoolean();
        ChatBotBuilder builder = new ChatBotBuilder() {{
            on("start", (message, commandLine) -> {
                if (!start.compareAndSet(false, true)) {
                    fail();
                }
            });
        }};

        ChatBot chatBot = builder.build();
        chatBot.replyOn(message("/start"));
        chatBot.replyOn(message("/unknown"));
        chatBot.replyOn(message("notcommand"));
        assertThat(start.get(), is(true));
    }
}