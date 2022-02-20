package codes.anhgelus.levelBot.manager;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class LevelManager {

    private final String userId;
    private final String guildId;

    private final MessageReceivedEvent event;

    private final ExperienceManager experienceManager;

    public LevelManager(MessageReceivedEvent event) {
        this.event = event;
        this.userId = event.getAuthor().getId();
        this.guildId = event.getGuild().getId();
        this.experienceManager = new ExperienceManager(event);
    }

    public int getLevel() {
        final int xp = this.experienceManager.getExperience();
        return getLevelWithXp(xp);
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
        int realLvl = 0;
        try {
            realLvl = Integer.parseInt(lvl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    public void newLevelEvent() {
        final MessageChannel channel = this.event.getChannel();

        final int level = getLevel() + 1;

        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key1 = guildId + ":grade-level:" + level;

        try (Jedis jedis = pool.getResource()) {
            final String grade = jedis.get(key1);

            if (grade != null) {
                final Role role = event.getGuild().getRoleById(Long.parseLong(grade));
                try {
                    event.getGuild().addRoleToMember(userId, role).queue();
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
