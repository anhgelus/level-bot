package codes.anhgelus.levelBot.manager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ExperienceManager {

    public static void addExperience(MessageReceivedEvent event, int experience) {
        final String userId = event.getAuthor().getId();
        final String guildId = event.getGuild().getId();

        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = guildId + ":" + userId + ":xp";

        try (Jedis jedis = pool.getResource()) {
            final String expStr = jedis.get(key);

            int exp = 0;

            if (expStr != null) {
                exp = Integer.parseInt(expStr);
            }

            final int newXp = exp + experience;

            if (LevelManager.isNewLevel(exp, newXp)) {
                LevelManager.newLevelEvent(event);
            }

            jedis.set(key, String.valueOf(newXp));
        }
        pool.close();
    }

    public static int getExperience(String userId, String guildId) {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = guildId + ":" + userId + ":xp";
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
