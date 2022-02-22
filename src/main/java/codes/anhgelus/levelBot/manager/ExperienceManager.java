package codes.anhgelus.levelBot.manager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExperienceManager {

    private final String userId;
    private final String guildId;

    private final MessageReceivedEvent event;

    public final static String USER_XP_HASH = "xp";

    public ExperienceManager(MessageReceivedEvent event) {
        this.event = event;
        this.userId = event.getAuthor().getId();
        this.guildId = event.getGuild().getId();
    }

    public void addExperience(int experience) {
        final String userId = this.event.getAuthor().getId();
        final String guildId = this.event.getGuild().getId();

        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final LevelManager levelManager = new LevelManager(this.event);
        final LeaderboardManager leaderboardManager = new LeaderboardManager(guildId, userId);

        try (Jedis jedis = pool.getResource()) {
            final String sampleUserIdStr = jedis.get(RedisManager.createUsersIdKey(guildId));

            int exp = 0;

            if (!Objects.equals(sampleUserIdStr, userId)) {
                jedis.set(RedisManager.createUsersIdKey(guildId), RedisManager.createUserIdValue(userId));
            }

            final int newXp = exp + experience;

            if (LevelManager.isNewLevel(exp, newXp)) {
                levelManager.newLevelEvent();
            }

            jedis.set(key, String.valueOf(newXp));
        }
        pool.close();
    }

    public int getExperience() {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = this.guildId + ":" + this.userId + ":xp";
        int exp = 0;

        try (Jedis jedis = pool.getResource()) {
            final String expStr = jedis.get(key);

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
