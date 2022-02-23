package codes.anhgelus.levelBot.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

public class ExperienceManager {

    private final String userId;
    private final String guildId;

    public final static String USER_XP_HASH = "xp";

    public ExperienceManager(String guildId, String userId) {
        this.userId = userId;
        this.guildId = guildId;
    }

    public void addExperience(int experience, Event event, JDA api) {
        final String userId = this.userId;
        final String guildId = this.guildId;

        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final LevelManager levelManager = new LevelManager(guildId, userId);

        try (Jedis jedis = pool.getResource()) {
            final String sampleUserIdStr = jedis.get(RedisManager.createUsersIdKey(guildId));

            int exp = 0;

            if (!Objects.equals(sampleUserIdStr, userId)) {
                jedis.set(RedisManager.createUsersIdKey(guildId), RedisManager.createUserIdValue(userId));
            }

            final String oldXp = jedis.hget(RedisManager.createUserKey(guildId, userId), RedisManager.XP_HASH);

            if (oldXp != null) exp = Integer.parseInt(oldXp);

            final int newXp = exp + experience;

            MessageChannel channel;

            if (event instanceof MessageReceivedEvent) {
                channel = ((MessageReceivedEvent) event).getChannel();
            } else {
                final String channelId = jedis.get(RedisManager.setupKey(guildId));
                if (channelId == null) {
                    api.getGuildById(guildId).getOwner().getUser().openPrivateChannel().queue((chan) -> {
                        chan.sendMessage("You don't set a default channel! Please setup it.\nTo setup it, use !setup default-channel {channel id}").queue();
                    });
                    return;
                }
                channel = api.getTextChannelById(channelId);
            }

            if (LevelManager.isNewLevel(exp, newXp)) {
                levelManager.newLevelEvent(newXp, channel, api);
            }

            jedis.hset(RedisManager.createUserKey(guildId, userId), RedisManager.createUserValue(String.valueOf(newXp), "", "", ""));
        }
        pool.close();
    }

    public int getExperience() {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = RedisManager.createUserKey(this.guildId, this.userId);
        int exp = 0;

        try (Jedis jedis = pool.getResource()) {
            final String expStr = jedis.hget(key, RedisManager.XP_HASH);

            if (expStr != null) {
                exp = Integer.parseInt(expStr);
            }
        }
        pool.close();

        return exp;
    }

    public static int experienceCalculator(String message) {
        int length = message.length();
        long chars = message.chars().distinct().count();

        return Math.round(experienceFormula(length, chars));
    }

    private static float experienceFormula(int length, long chars) {
        // f(x)=((0.025 x^(1.25))/(50^(-0.5)))+1
        float result = (float) (0.025f * Math.pow(length, 1.25));
        result = (float) (result / Math.pow(chars, -0.5));
        return result + 1;
    }

}
