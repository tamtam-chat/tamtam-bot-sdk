package chat.tamtam.bot;


import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.AttachmentRequest;
import chat.tamtam.botapi.model.NewMessageBody;
import chat.tamtam.botapi.model.NewMessageLink;
import chat.tamtam.botapi.model.SendMessageResult;
import chat.tamtam.botapi.model.TextFormat;
import chat.tamtam.botapi.queries.SendMessageQuery;
import chat.tamtam.botapi.queries.TamTamQuery;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author almaz.shakirov
 */
public class MessageSenderTest {
    @Test
    public void testSendMessage() throws ClientException, ExecutionException, InterruptedException {
        // mock
        TamTamClient tamTamClient = mock(TamTamClient.class);
        Future<SendMessageResult> future = mock(Future.class);
        when(tamTamClient.newCall(any(TamTamQuery.class))).thenReturn(future);
        when(future.get()).thenReturn(mock(SendMessageResult.class));

        // test data
        String randomStr = getRandomString(3999);
        String text = randomStr + "👨‍👩‍👧‍👦";
        String[] expected = new String[]{randomStr, "👨‍👩‍👧‍👦"};
        long userId = 373147507928L;
        List<AttachmentRequest> list = Collections.singletonList(mock(AttachmentRequest.class));
        NewMessageLink link = mock(NewMessageLink.class);
        NewMessageBody messageBodyOriginal = new NewMessageBody(text, list, link);
        messageBodyOriginal.setFormat(TextFormat.HTML);
        messageBodyOriginal.setNotify(true);

        // invoke testing method
        MessageSender messageSender = new MessageSender(tamTamClient);
        messageSender.sendMessage(userId, messageBodyOriginal);

        // assert
        ArgumentCaptor<SendMessageQuery> argument = ArgumentCaptor.forClass(SendMessageQuery.class);
        verify(tamTamClient, times(2)).newCall(argument.capture());

        NewMessageBody messageBody1 = new NewMessageBody(expected[0], list, link);
        NewMessageBody messageBody2 = new NewMessageBody(expected[1], null, null);

        assertThat(argument.getAllValues().get(0).getBody(), not(messageBody1));
        assertThat(argument.getAllValues().get(1).getBody(), not(messageBody2));

        messageBody1.setFormat(TextFormat.HTML);
        messageBody1.setNotify(true);
        messageBody2.setFormat(TextFormat.HTML);
        messageBody2.setNotify(true);

        assertThat(argument.getAllValues().get(0).getBody(), is(messageBody1));
        assertThat(argument.getAllValues().get(1).getBody(), is(messageBody2));
    }

    @Test
    public void testSplitTextByMaxCharsInMessage1() {
        String s = "";
        String[] expected = {""};
        assertThat(MessageSender.splitTextByMaxCharsInMessage(s), is(expected));
    }

    @Test
    public void testSplitTextByMaxCharsInMessage2() {
        String s = "a";
        String[] expected = {"a"};
        assertThat(MessageSender.splitTextByMaxCharsInMessage(s), is(expected));
    }

    @Test
    public void testSplitTextByMaxCharsInMessage3() {
        String randomStr = getRandomString(3999);
        String text = randomStr + "👨‍👩‍👧‍👦";
        String[] expected = new String[]{randomStr, "👨‍👩‍👧‍👦"};
        assertThat(MessageSender.splitTextByMaxCharsInMessage(text), is(expected));
    }

    /**
     * Test case while Zero Width Joiner on position with index 3999
     */
    @Test
    public void testSplitTextByMaxCharsInMessage4() {
        String randomStr = getRandomString(3997);
        String text = randomStr + "👨‍👩‍👧‍👦";
        String[] expected = new String[]{randomStr, "👨‍👩‍👧‍👦"};
        assertThat(MessageSender.splitTextByMaxCharsInMessage(text), is(expected));
    }

    private String getRandomString(int length) {
        return new Random().ints(97, 122 + 1) // 97 - 'a', 122 - 'z'
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}