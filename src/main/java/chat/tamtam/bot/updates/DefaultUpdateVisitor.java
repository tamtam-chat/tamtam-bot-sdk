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
public abstract class DefaultUpdateVisitor implements Update.Visitor {
    @Override
    public void visit(MessageCreatedUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(MessageCallbackUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(MessageEditedUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(MessageRemovedUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(BotAddedToChatUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(BotRemovedFromChatUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(UserAddedToChatUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(UserRemovedFromChatUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(BotStartedUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(ChatTitleChangedUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(MessageConstructionRequest model) {
        visitDefault(model);
    }

    @Override
    public void visit(MessageConstructedUpdate model) {
        visitDefault(model);
    }

    @Override
    public void visit(MessageChatCreatedUpdate model) {
        visitDefault(model);
    }
}
