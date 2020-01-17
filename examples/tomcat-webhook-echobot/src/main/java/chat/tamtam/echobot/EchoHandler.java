package chat.tamtam.echobot;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import chat.tamtam.botapi.client.TamTamClient;
import chat.tamtam.botapi.exceptions.APIException;
import chat.tamtam.botapi.exceptions.ClientException;
import chat.tamtam.botapi.model.BotAddedToChatUpdate;
import chat.tamtam.botapi.model.BotRemovedFromChatUpdate;
import chat.tamtam.botapi.model.BotStartedUpdate;
import chat.tamtam.botapi.model.ChatTitleChangedUpdate;
import chat.tamtam.botapi.model.MessageCallbackUpdate;
import chat.tamtam.botapi.model.MessageChatCreatedUpdate;
import chat.tamtam.botapi.model.MessageConstructedUpdate;
import chat.tamtam.botapi.model.MessageConstructionRequest;
import chat.tamtam.botapi.model.MessageCreatedUpdate;
import chat.tamtam.botapi.model.MessageEditedUpdate;
import chat.tamtam.botapi.model.MessageRemovedUpdate;
import chat.tamtam.botapi.model.NewMessageBody;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.UserAddedToChatUpdate;
import chat.tamtam.botapi.model.UserRemovedFromChatUpdate;
import chat.tamtam.botapi.queries.SendMessageQuery;
import chat.tamtam.botapi.queries.TamTamQuery;

/**
 * @author alexandrchuprin
 */
public class EchoHandler implements Update.Visitor {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TamTamClient client;
    private final ObjectMapper mapper;

    EchoHandler(TamTamClient client) {
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void visit(MessageCreatedUpdate update) {
        Long chatId = Objects.requireNonNull(update.getMessage().getRecipient().getChatId(), "chatId");
        sendToChat(chatId, update);
    }

    @Override
    public void visit(MessageCallbackUpdate update) {
        sendToUser(update.getCallback().getUser().getUserId(), update);
    }

    @Override
    public void visit(MessageEditedUpdate update) {
        long chatId = Objects.requireNonNull(update.getMessage().getRecipient().getChatId(), "chatId");
        sendToChat(chatId, update);
    }

    @Override
    public void visit(MessageRemovedUpdate update) {

    }

    @Override
    public void visit(BotAddedToChatUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(BotRemovedFromChatUpdate update) {
        sendToUser(update.getUser().getUserId(), update);
    }

    @Override
    public void visit(UserAddedToChatUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(UserRemovedFromChatUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(BotStartedUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(ChatTitleChangedUpdate update) {
        sendToChat(update.getChatId(), update);
    }

    @Override
    public void visit(MessageConstructionRequest update) {
        sendToUser(update.getUser().getUserId(), update);
    }

    @Override
    public void visit(MessageConstructedUpdate update) {
        sendToUser(update.getMessage().getSender().getUserId(), update);
    }

    @Override
    public void visit(MessageChatCreatedUpdate update) {
        sendToUser(Objects.requireNonNull(update.getChat().getOwnerId(), "ownerId"), update);
    }

    @Override
    public void visitDefault(Update update) {
        LOG.warn("Update {} is unsupported", update);
    }

    private void sendToChat(long chatId, Update update) {
        try {
            sendSafely(prepareQuery(update).chatId(chatId));
        } catch (ClientException | JsonProcessingException e) {
            LOG.error("Failed to prepare request to send update {} to chat {}", update, chatId, e);
        }
    }

    private void sendToUser(long userId, Update update) {
        try {
            sendSafely(prepareQuery(update).userId(userId));
        } catch (ClientException | JsonProcessingException e) {
            LOG.error("Failed to prepare request to send update {} to user {}", update, userId, e);
        }
    }

    private SendMessageQuery prepareQuery(Update update) throws ClientException, JsonProcessingException {
        String text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(update);
        return new SendMessageQuery(client, new NewMessageBody(text, null, null));
    }

    private void sendSafely(TamTamQuery<?> query) {
        try {
            query.execute();
        } catch (APIException | ClientException e) {
            LOG.error("Failed to execute query {}", query, e);
        }
    }
}
