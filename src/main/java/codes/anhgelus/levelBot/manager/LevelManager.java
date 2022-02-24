package codes.anhgelus.levelBot.manager;

import codes.anhgelus.levelBot.commands.SetupCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class LevelManager {

    private final String userId;
    private final String guildId;

    private final ExperienceManager experienceManager;

    public LevelManager(String guildId, String userId) {
        this.userId = userId;
        this.guildId = guildId;
        this.experienceManager = new ExperienceManager(guildId, userId);
    }

    public int getLevel() {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = RedisManager.createUserKey(this.guildId, this.userId);

        int level = 0;

        try (Jedis jedis = pool.getResource()) {
            final String oldLvl = jedis.hget(key, RedisManager.LEVEL_HASH);
            if (oldLvl != null) {
                try {
                    level = Integer.parseInt(oldLvl);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        if (level == 0) {
            level = getLevelWithXp(experienceManager.getExperience());
        }
        pool.close();
        return level;
    }

    public static int getLevelXpTotal(int lvl) {
        /*
        * f(x) = lvl
        * f(x) = 0.1 * x^0.5
        * lvl = 0.1 * x^0.5
        * lvl/0.1 = x^0.5
        * Math.sqrt(lvl/0.1) = x
         */
        long xp = Math.round(Math.pow((lvl / 0.1), 0.5*4));
        if (xp > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.toIntExact(xp);
    }

    public static int getLevelXpTotal(String lvl) {
        int realLvl;
        try {
            realLvl = Integer.parseInt(lvl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -2;
        }
        if (realLvl < 0) {
            return -2;
        }
        long xp = Math.round(Math.pow((realLvl / 0.1), 0.5*4));
        if (xp > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.toIntExact(xp);
    }

    public static boolean isNewLevel(int oldXp, int newXp) {
        return getLevelWithXp(oldXp) != getLevelWithXp(newXp);
    }

    public static int getLevelWithXp(int xp) {
        final long lvl = Math.round(Math.floor(levelFormula(xp)));
        if (lvl > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.toIntExact(lvl);
    }

    public void newLevelEvent(int xp, MessageChannel channel, JDA api) {
        final int level = getLevelWithXp(xp);

        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key1 = RedisManager.setupKey(this.guildId);
        final String key2 = RedisManager.createUserKey(this.guildId, this.userId);

        try (Jedis jedis = pool.getResource()) {
            final SetupManager setupManager = new SetupManager(jedis);

            if (setupManager.isGradeLevel(key1, level)) {
                final Guild guild = api.getGuildById(this.guildId);
                final Role role = guild.getRoleById(Long.parseLong(setupManager.getGradeLevel(key1, level)));

                try {
                    guild.addRoleToMember(this.userId, role).queue();
                    jedis.hset(key2, RedisManager.createUserValue("", String.valueOf(level), "", ""));
                } catch (Exception e) {
                    channel.sendMessage(e.getMessage()).queue();
                    return;
                }
            }
        }
        pool.close();

        channel.sendMessage("<@" + userId + ">, you just reached the level " + level + ", GG!").queue();
    }

    private static double levelFormula(int xp) {
        // f(x)=0.1*x^(0.5)
        return 0.1 * Math.pow(xp, 0.5);
    }

}
