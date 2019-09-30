package chat.tamtam.bot.webhook;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chat.tamtam.bot.Randoms;
import chat.tamtam.bot.webhook.jetty.JettyWebhookBotContainer;
import chat.tamtam.botapi.TamTamBotAPI;
import chat.tamtam.botapi.client.ClientResponse;
import chat.tamtam.botapi.client.TamTamSerializer;
import chat.tamtam.botapi.client.TamTamTransportClient;
import chat.tamtam.botapi.client.impl.JacksonSerializer;
import chat.tamtam.botapi.client.impl.OkHttpTransportClient;
import chat.tamtam.botapi.model.SubscriptionRequestBody;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.queries.SubscribeQuery;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        TamTamBotAPI api = mock(TamTamBotAPI.class);
        SubscribeQuery query = mock(SubscribeQuery.class);
        when(api.subscribe(any(SubscriptionRequestBody.class))).thenReturn(query);

        container = new JettyWebhookBotContainer("0.0.0.0", 12345);
        bot = new TestBot(api, "/testbot");
        httpClient = new OkHttpTransportClient();
        serializer = new JacksonSerializer();

        container.register(bot);
        container.start();
    }

    @After
    public void tearDown() throws Exception {
        container.stop();
        container.join();
    }

    @Test
    public void name() throws Exception {
        Set<Update> updates = Stream.generate(Randoms::randomUpdate).limit(100).collect(Collectors.toSet());
        List<Future<ClientResponse>> responses = new ArrayList<>(updates.size());
        for (Update update : updates) {
            byte[] bytes = serializer.serialize(update);
            String url = "http://0.0.0.0:12345/testbot";
            responses.add(httpClient.post(url, bytes));
        }

        for (Future<ClientResponse> response : responses) {
            response.get();
        }

        Thread.sleep(5_000);

        assertThat(bot.receivedUpdates, is(updates));
    }

    private class TestBot extends WebhookBot {
        Set<Update> receivedUpdates = ConcurrentHashMap.newKeySet();

        TestBot(TamTamBotAPI api, String path) {
            super(api, WebhookBotOptions.DEFAULT, path);
        }

        @Override
        public void onUpdate(Update update) {
            receivedUpdates.add(update);
        }
    }
}