package chat.tamtam.bot.webhook;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chat.tamtam.bot.webhook.jetty.JettyWebhookBotContainer;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.client.TamTamSerializer;
import chat.tamtam.botapi.client.TamTamTransportClient;
import chat.tamtam.botapi.model.Update;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * @author alexandrchuprin
 */
public class WebhookBotTest {
    private TestBot bot;
    private JettyWebhookBotContainer container;
    private TamTamTransportClient httpClient;
    private TamTamSerializer serializer;

    @Before
    public void setUp() throws Exception {
//        TamTamClient client = mock(TamTamClient.class);
//        SubscribeQuery query = mock(SubscribeQuery.class);
//        when(client.subscribe(any(SubscriptionRequestBody.class))).thenReturn(query);
//
//        container = new JettyWebhookBotContainer("0.0.0.0", 12345);
//        bot = new TestBot(client, "/testbot");
//        httpClient = new OkHttpTransportClient();
//        serializer = new JacksonSerializer();
//
//        container.register(bot);
//        container.start();
    }

    @After
    public void tearDown() throws Exception {
//        container.stop();
//        container.join();
    }

    @Test
    public void shouldReceiveWebhooks() throws Exception {
//        Set<Update> updates = Stream.generate(Randoms::randomUpdate).limit(100).collect(Collectors.toSet());
//        List<Future<ClientResponse>> responses = new ArrayList<>(updates.size());
//        for (Update update : updates) {
//            byte[] bytes = serializer.serialize(update);
//            String url = "http://0.0.0.0:12345/testbot";
//            responses.add(httpClient.post(url, bytes));
//        }
//
//        for (Future<ClientResponse> response : responses) {
//            response.get();
//        }
//
//        Thread.sleep(5_000);
//
//        assertThat(bot.receivedUpdates, is(updates));
    }

    private class TestBot extends WebhookBot {
        Set<Update> receivedUpdates = ConcurrentHashMap.newKeySet();

        TestBot(TamTamClient client, String path) {
            super(client, WebhookBotOptions.DEFAULT);
        }

        @Override
        public Object onUpdate(Update update) {
            receivedUpdates.add(update);
            return null;
        }
    }
}