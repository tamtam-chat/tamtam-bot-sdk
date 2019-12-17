package chat.tamtam.bot;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import chat.tamtam.bot.annotations.UpdateHandler;
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
    public void shouldHandleUpdate() {
        Message message = mock(Message.class);
        Update update = new MessageCreatedUpdate(message, 1L);
        Object response = testBot.onUpdate(update);
        testBot.verify();
        assertThat(response, is(nullValue()));
    }

    @Test
    public void shouldHandleUpdateReturnNoResponse() {
        Update update = new BotStartedUpdate(1L, mock(User.class), 1L);
        Object response = testBot.onUpdate(update);
        testBot.verify();
        assertThat(response, is(nullValue()));
    }

    @Test
    public void shouldReturnAnyResponse() {
        Update update = new MessageRemovedUpdate("asd", 1L, 2L, 3L);
        Object response = testBot.onUpdate(update);
        testBot.verify();
        assertThat(response, is(mockResponse));
    }

    @Test
    public void shouldNotHandleUpdate() {
        Update update = new MessageEditedUpdate(mock(Message.class), 1L);
        Object response = testBot.onUpdate(update);
        assertThat(response, is(nullValue()));
    }

    @Test
    public void shouldInvokePrivateMethod() {
        BotWithPrivateMethod bot = new BotWithPrivateMethod(client);
        Update update = new MessageCreatedUpdate(mock(Message.class), 1L);
        Object response = bot.onUpdate(update);
        assertThat(response, is(mockResponse));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInvokeMethodWithManyArgs() {
        new InvalidBot2(client);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInvokeMethodWithNonUpdateArg() {
        new InvalidBot3(client);
    }

    @Test
    public void shouldRegisterHandlers() {
        AtomicBoolean handled = new AtomicBoolean();
        AtomicBoolean handled2 = new AtomicBoolean();
        Object handler1 = new Object() {
            @UpdateHandler
            public void handle(MessageCreatedUpdate update) {
                handled.compareAndSet(false, true);
            }
        };

        Object handler2 = new Object() {
            @UpdateHandler
            public void handle(MessageEditedUpdate update) {
                handled2.compareAndSet(false, true);
            }
        };

        TamTamBot bot = new TamTamBotBase(client, handler1, handler2);
        bot.onUpdate(new MessageCreatedUpdate(mock(Message.class), System.currentTimeMillis()));
        bot.onUpdate(new MessageEditedUpdate(mock(Message.class), System.currentTimeMillis()));
        assertThat(handled.get(), is(true));
        assertThat(handled2.get(), is(true));
    }

    private class TestBot extends TamTamBotBase {
        private final AtomicBoolean handled;

        TestBot(TamTamClient client) {
            super(client);
            this.handled = new AtomicBoolean();
        }

        @UpdateHandler
        public void onMessageCreated(MessageCreatedUpdate update) {
            signal();
        }

        @UpdateHandler
        public Object onBotStarted(BotStartedUpdate update) {
            signal();
            return null;
        }

        @UpdateHandler
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

    private class BotWithPrivateMethod extends TamTamBotBase {
        BotWithPrivateMethod(TamTamClient client) {
            super(client);
        }

        @UpdateHandler
        private NewMessageBody onMessageCreated(MessageCreatedUpdate update) {
            return mockResponse;
        }
    }

    private class InvalidBot2 extends TamTamBotBase {
        InvalidBot2(TamTamClient client) {
            super(client);
        }

        @UpdateHandler
        public NewMessageBody onMessageCreated(MessageCreatedUpdate update, Object arg2) {
            fail();
            return mockResponse;
        }
    }

    private class InvalidBot3 extends TamTamBotBase {
        InvalidBot3(TamTamClient client) {
            super(client);
        }

        @UpdateHandler
        public NewMessageBody onMessageCreated(Object arg) {
            fail();
            return mockResponse;
        }
    }
}
