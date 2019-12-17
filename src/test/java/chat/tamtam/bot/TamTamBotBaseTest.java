package chat.tamtam.bot;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import chat.tamtam.bot.annotations.OnUpdate;
import chat.tamtam.bot.builders.NewMessageBodyBuilder;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.Message;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import chat.tamtam.botapi.model.MessageEditedUpdate;
import chat.tamtam.botapi.model.MessageRemovedUpdate;
import chat.tamtam.botapi.model.NewMessageBody;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.User;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * @author alexandrchuprin
 */
public class TamTamBotBaseTest {

    private NewMessageBody mockResponse = NewMessageBodyBuilder.ofText("test").build();
    private TamTamClient client = mock(TamTamClient.class);
    private TestBot testBot;

    @Before
    public void setUp() {
        testBot = new TestBot(client);
    }

    @Test
    public void shouldHandleUpdate() throws Exception {
        Message message = mock(Message.class);
        Update update = new MessageCreatedUpdate(message, 1L);
        Object response = testBot.onUpdate(update);
        testBot.verify();
        assertThat(response, is(nullValue()));
    }

    @Test
    public void shouldHandleUpdateReturnNoResponse() throws Exception {
        Update update = new BotStartedUpdate(1L, mock(User.class), 1L);
        Object response = testBot.onUpdate(update);
        testBot.verify();
        assertThat(response, is(nullValue()));
    }

    @Test
    public void shouldReturnAnyResponse() throws Exception {
        Update update = new MessageRemovedUpdate("asd", 1L, 2L, 3L);
        Object response = testBot.onUpdate(update);
        testBot.verify();
        assertThat(response, is(mockResponse));
    }

    @Test
    public void shouldNotHandleUpdate() throws Exception {
        Update update = new MessageEditedUpdate(mock(Message.class), 1L);
        Object response = testBot.onUpdate(update);
        assertThat(response, is(nullValue()));
    }

    @Test
    public void shouldInvokePrivateMethod() throws Exception {
        InvalidBot1 bot = new InvalidBot1(client);
        Update update = new MessageCreatedUpdate(mock(Message.class), 1L);
        Object response = bot.onUpdate(update);
        assertThat(response, is(mockResponse));
    }

    @Test
    public void shouldNotInvokeMethodWithManyArgs() throws Exception {
        InvalidBot2 bot = new InvalidBot2(client);
        Update update = new MessageCreatedUpdate(mock(Message.class), 1L);
        Object response = bot.onUpdate(update);
        assertThat(response, is(nullValue()));
    }

    @Test
    public void shouldNotInvokeMethodWithNonUpdateArg() throws Exception {
        InvalidBot3 bot = new InvalidBot3(client);
        Update update = new MessageCreatedUpdate(mock(Message.class), 1L);
        Object response = bot.onUpdate(update);
        assertThat(response, is(nullValue()));
    }

    private class TestBot extends TamTamBotBase {
        private final AtomicBoolean handled;

        TestBot(TamTamClient client) {
            super(client);
            this.handled = new AtomicBoolean();
        }

        @OnUpdate(Update.MESSAGE_CREATED)
        public void onMessageCreated(MessageCreatedUpdate update) {
            signal();
        }

        @OnUpdate(Update.BOT_STARTED)
        public Object onBotStarted(BotStartedUpdate update) {
            signal();
            return null;
        }

        @OnUpdate(Update.MESSAGE_REMOVED)
        public NewMessageBody onMessageRemoved(MessageRemovedUpdate update) {
            signal();
            return mockResponse;
        }

        private void signal() {
            if (!handled.compareAndSet(false, true)) {
                fail();
            }
        }

        private void verify() {
            assertThat(handled.get(), is(true));
        }
    }

    private class InvalidBot1 extends TamTamBotBase {
        InvalidBot1(TamTamClient client) {
            super(client);
        }

        @OnUpdate(Update.MESSAGE_CREATED)
        private NewMessageBody onMessageCreated(MessageCreatedUpdate update) {
            return mockResponse;
        }
    }

    private class InvalidBot2 extends TamTamBotBase {
        InvalidBot2(TamTamClient client) {
            super(client);
        }

        @OnUpdate(Update.MESSAGE_CREATED)
        public NewMessageBody onMessageCreated(MessageCreatedUpdate update, Object arg2) {
            return mockResponse;
        }
    }

    private class InvalidBot3 extends TamTamBotBase {
        InvalidBot3(TamTamClient client) {
            super(client);
        }

        @OnUpdate(Update.MESSAGE_CREATED)
        public NewMessageBody onMessageCreated(Object arg) {
            return mockResponse;
        }
    }
}
