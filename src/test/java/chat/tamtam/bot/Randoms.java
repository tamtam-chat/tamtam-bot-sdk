package chat.tamtam.bot;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import chat.tamtam.botapi.model.BotAddedToChatUpdate;
import chat.tamtam.botapi.model.Update;
import chat.tamtam.botapi.model.User;

/**
 * @author alexandrchuprin
 */
public class Randoms {
    private static final AtomicLong NEXT_ID = new AtomicLong();

    public static String text() {
        return UUID.randomUUID().toString();
    }

    public static Update randomUpdate() {
        User user = randomUser();
        return new BotAddedToChatUpdate(nextId(), user, nextId());
    }

    public static User randomUser() {
        long userId = nextId();
        String name = "Name " + userId;
        String username = "username" + userId;
        return new User(userId, name, username, randomBoolean(), randomLong());
    }

    public static double randomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static <T> T random(List<T> updates) {
        return updates.get(randomInt(updates.size()));
    }

    public static int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static long randomLong() {return ThreadLocalRandom.current().nextLong();}

    private static long nextId() {
        return NEXT_ID.incrementAndGet();
    }

}
