package chat.tamtam.bot.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Test;

import chat.tamtam.bot.Mocks;
import chat.tamtam.bot.chat.ChatBot;
import chat.tamtam.bot.chat.CommandHandler;
import chat.tamtam.botapi.model.LocationAttachment;
import chat.tamtam.botapi.model.Message;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author alexandrchuprin
 */
public class ChatBotTest {
    @Test
    public void shouldExecuteCommandHandler() {
        AtomicBoolean command1Executed = new AtomicBoolean();
        AtomicBoolean command2Executed = new AtomicBoolean();
        AtomicInteger defaultHandlerExecuted = new AtomicInteger();
        AtomicBoolean unknownCommandHandlerExecuted = new AtomicBoolean();
        Map<String, CommandHandler> handlers = new HashMap<String, CommandHandler>() {{
            put("command1", (message, commandLine) -> {
                command1Executed.set(true);
                assertThat(commandLine.getKey(), is("command1"));
                assertThat(commandLine.getArgs(), is(new String[]{"arg"}));
            });

            put("command2", (message, commandLine) -> {
                command2Executed.set(true);
                assertThat(commandLine.getKey(), is("command2"));
                assertThat(commandLine.getArgs(), is(new String[]{"arg"}));
            });
        }};

        Consumer<Message> defaultHandler = message -> {
            int times = defaultHandlerExecuted.getAndIncrement();
            if (times == 0) {
                assertThat(message.getBody().getText(), is("notcommand"));
            } else if (times == 1) {
                assertThat(message.getBody().getText(), is(nullValue()));
                assertThat(message.getBody().getAttachments().size(), is(greaterThan(0)));
            }
        };

        CommandHandler unknownCommandHandler = (message, commandLine) -> {
            unknownCommandHandlerExecuted.set(true);
            assertThat(commandLine.getKey(), is("command3"));
            assertThat(commandLine.getArgs(), is(new String[]{"arg"}));
        };

        ChatBot chatBot = new ChatBot(handlers, defaultHandler, unknownCommandHandler);
        chatBot.replyOn(Mocks.message("/command1 arg"));
        chatBot.replyOn(Mocks.message("/command2 arg"));
        chatBot.replyOn(Mocks.message("/command3 arg"));
        chatBot.replyOn(Mocks.message("notcommand"));
        chatBot.replyOn(Mocks.message(null, Collections.singletonList(new LocationAttachment(1d, 1d))));

        assertThat(command1Executed.get(), is(true));
        assertThat(command2Executed.get(), is(true));
        assertThat(defaultHandlerExecuted.get(), is(2));
    }
}