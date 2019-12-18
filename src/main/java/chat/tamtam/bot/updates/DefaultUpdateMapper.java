package chat.tamtam.bot.updates;

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
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.UserAddedToChatUpdate;
import chat.tamtam.botapi.model.UserRemovedFromChatUpdate;

/**
 * @author alexandrchuprin
 */
public abstract class DefaultUpdateMapper<T> implements Update.Mapper<T> {
    @Override
    public T map(MessageCreatedUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(MessageCallbackUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(MessageEditedUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(MessageRemovedUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(BotAddedToChatUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(BotRemovedFromChatUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(UserAddedToChatUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(UserRemovedFromChatUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(BotStartedUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(ChatTitleChangedUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(MessageConstructionRequest model) {
        return mapDefault(model);
    }

    @Override
    public T map(MessageConstructedUpdate model) {
        return mapDefault(model);
    }

    @Override
    public T map(MessageChatCreatedUpdate model) {
        return mapDefault(model);
    }
}
