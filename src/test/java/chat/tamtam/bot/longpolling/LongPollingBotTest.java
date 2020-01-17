package chat.tamtam.bot.longpolling;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

/**
 * @author alexandrchuprin
 */
@RunWith(MockitoJUnitRunner.class)
public class LongPollingBotTest {
    @Mock
    private TamTamClient client;
    List<Update> allUpdates;

    @Before
    public void setUp() throws Exception {
        allUpdates = Stream.generate(Randoms::randomUpdate).limit(945).collect(Collectors.toList());
        when(client.newCall(isA(GetUpdatesQuery.class))).thenAnswer(i -> {
            GetUpdatesQuery query = i.getArgumentAt(0, GetUpdatesQuery.class);
            long from = query.marker.getValue() == null ? 0 : query.marker.getValue();
            if (from >= allUpdates.size()) {
                return CompletableFuture.completedFuture(new UpdateList(Collections.emptyList(), null));
            }

            long limit = query.limit.getValue() == null ? 100 : query.limit.getValue();
            int to = (int) Math.min(allUpdates.size(), from + limit);
            List<Update> sublist = allUpdates.subList(Math.toIntExact(from), to);
            return CompletableFuture.completedFuture(new UpdateList(sublist, from + sublist.size()));
        });
    }

    @Test
    public void shouldHandleUpdates() throws Exception {
        GetSubscriptionsResult subscriptionsResult = new GetSubscriptionsResult(Collections.emptyList());
        when(client.newCall(isA(GetSubscriptionsQuery.class)))
                .thenReturn(CompletableFuture.completedFuture(subscriptionsResult));

        TestBot bot = new TestBot(client, new HashSet<>(allUpdates));
        bot.start();
        bot.await();
        bot.stop();
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