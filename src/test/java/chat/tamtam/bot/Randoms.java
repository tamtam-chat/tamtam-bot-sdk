package chat.tamtam.bot;

import java.util.concurrent.atomic.AtomicLong;

import chat.tamtam.botapi.model.BotAddedToChatUpdate;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.User;

/**
 * @author alexandrchuprin
 */
public class Randoms {
    private static final AtomicLong NEXT_ID = new AtomicLong();

    public static Update randomUpdate() {
        User user = randomUser();
        return new BotAddedToChatUpdate(nextId(), user, nextId());
    }

    public static User randomUser() {
        long userId = nextId();
        String name = "Name " + userId;
        String username = "username" + userId;
        return new User(userId, name, username);
    }

    private static long nextId() {
        return NEXT_ID.incrementAndGet();
    }

}
