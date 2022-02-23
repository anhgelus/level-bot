package codes.anhgelus.levelBot.manager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Timestamp;

public class VoiceManager {

    private final String guildId;
    private final String userId;

    public VoiceManager(String guildId, String userId) {
        this.guildId = guildId;
        this.userId = userId;
    }

    public void connection(Timestamp timestamp) {
        final RedisManager rm = new RedisManager();
        final JedisPool pool = rm.getPool();

        try (Jedis jedis = pool.getResource()) {
            jedis.hset(RedisManager.createUserKey(this.guildId, this.userId), RedisManager.createUserValue("", "", "", String.valueOf(timestamp.getTime())));
        }
        pool.close();
    }

    public void disconnection(Timestamp timestamp, GuildVoiceLeaveEvent event, JDA api) {
        final RedisManager rm = new RedisManager();
        final JedisPool pool = rm.getPool();

        try (Jedis jedis = pool.getResource()) {
            final String joinedStr = jedis.hget(RedisManager.createUserKey(this.guildId, this.userId), RedisManager.CONNECTION_HASH);

            long joined = 0;

            try {
                joined = Long.parseLong(joinedStr);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                pool.close();
                return;
            }

            final long timePassed = timestamp.getTime() - joined;

            if (timePassed < 0) {
                System.out.println("timePassed long var (37 - VoiceManager.java) is negative: " + timePassed);
                pool.close();
                return;
            }

            final ExperienceManager experienceManager = new ExperienceManager(event.getGuild().getId(), event.getMember().getId());
            int xp = experienceCalculator(timePassed);
            experienceManager.addExperience(xp, event, api);
        }
        pool.close();
    }

    public static int experienceCalculator(long length) {
        return Math.round(experienceFormula(length));
    }

    private static float experienceFormula(long length) {
        // f(x)=((0.25 x^(1.3))/(60000))+1
        float result = (float) (0.025f * Math.pow(length, 1.25))/60000;
        return result + 1;
    }

}
