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
public class NoopUpdateVisitor implements Update.Visitor {
    @Override
    public void visit(MessageCreatedUpdate model) {

    }

    @Override
    public void visit(MessageCallbackUpdate model) {

    }

    @Override
    public void visit(MessageEditedUpdate model) {

    }

    @Override
    public void visit(MessageRemovedUpdate model) {

    }

    @Override
    public void visit(BotAddedToChatUpdate model) {

    }

    @Override
    public void visit(BotRemovedFromChatUpdate model) {

    }

    @Override
    public void visit(UserAddedToChatUpdate model) {

    }

    @Override
    public void visit(UserRemovedFromChatUpdate model) {

    }

    @Override
    public void visit(BotStartedUpdate model) {

    }

    @Override
    public void visit(ChatTitleChangedUpdate model) {

    }

    @Override
    public void visit(MessageConstructionRequest model) {

    }

    @Override
    public void visit(MessageConstructedUpdate model) {

    }

    @Override
    public void visit(MessageChatCreatedUpdate model) {

    }

    @Override
    public void visitDefault(Update model) {

    }
}
