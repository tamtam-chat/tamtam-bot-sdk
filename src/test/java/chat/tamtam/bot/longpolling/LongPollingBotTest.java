package chat.tamtam.bot.longpolling;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import chat.tamtam.bot.Randoms;
import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.GetSubscriptionsResult;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.UpdateList;
import chat.tamtam.botapi.queries.GetSubscriptionsQuery;
import chat.tamtam.botapi.queries.GetUpdatesQuery;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author alexandrchuprin
 */
public class LongPollingBotTest {
    private TamTamClient client;

    @Before
    public void setUp() {
        client = mock(TamTamClient.class);
    }

    @Test
    public void shouldHandleUpdates() throws Exception {
//        List<Update> updates = Stream.generate(Randoms::randomUpdate).limit(945).collect(Collectors.toList());
//        when(client.getUpdates()).thenAnswer(i -> new MockGetUpdatesQuery(updates));
//
//        GetSubscriptionsQuery getSubsQuery = mock(GetSubscriptionsQuery.class);
//        when(getSubsQuery.execute()).thenReturn(new GetSubscriptionsResult(Collections.emptyList()));
//        when(client.getSubscriptions()).thenReturn(getSubsQuery);
//
//        TestBot bot = new TestBot(client, new HashSet<>(updates));
//        bot.start();
//        bot.await();
//        bot.stop();
    }

    private class MockGetUpdatesQuery extends GetUpdatesQuery {
        private final List<Update> allUpdates;
        private Long from;
        private Integer limit;

        MockGetUpdatesQuery(List<Update> allUpdates) {
            super(mock(TamTamClient.class));
            this.allUpdates = allUpdates;
        }

        @Override
        public GetUpdatesQuery marker(Long value) {
            this.from = value == null ? 0 : value;
            return super.marker(value);
        }

        @Override
        public GetUpdatesQuery limit(Integer value) {
            this.limit = value == null ? 100 : value;
            return super.limit(value);
        }

        @Override
        public UpdateList execute() {
            if (from >= allUpdates.size()) {
                return new UpdateList(Collections.emptyList(), null);
            }

            int to = (int) Math.min(allUpdates.size(), from + limit);
            List<Update> sublist = allUpdates.subList(Math.toIntExact(from), to);
            return new UpdateList(sublist, from + sublist.size());
        }
    }

    private class TestBot extends LongPollingBot {
        final Set<Update> expectedUpdates;
        CountDownLatch allReceived;

        TestBot(TamTamClient client, Set<Update> expectedUpdates) {
            super(client, LongPollingBotOptions.DEFAULT);
            this.expectedUpdates = expectedUpdates;
            this.allReceived = new CountDownLatch(expectedUpdates.size());
        }

        @Override
        public Object onUpdate(Update update) {
            if (!expectedUpdates.remove(update)) {
                throw new IllegalArgumentException("Non expected update: " + update);
            }

            allReceived.countDown();
            return null;
        }

        void await() throws InterruptedException {
            if (!allReceived.await(30, TimeUnit.SECONDS)) {
                fail("Not all updates received");
            }
        }

        @Override
        protected UpdateList pollOnce(Long marker) throws APIException, ClientException {
            System.out.println("Polling… Marker: " + marker);
            return super.pollOnce(marker);
        }
    }
}