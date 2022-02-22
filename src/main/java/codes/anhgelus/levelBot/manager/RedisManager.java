package codes.anhgelus.levelBot.manager;

import codes.anhgelus.levelBot.LevelBot;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class RedisManager {

    private final JedisPool pool;

    public final static String XP_HASH = "xp";
    public final static String LEVEL_HASH = "level";
    public final static String LEADERBOARD_HASH = "leaderboard";

    public RedisManager() {
        ConfigManager conf = new ConfigManager(LevelBot.CONF_FILE_NAME);

        this.pool = new JedisPool(new JedisPoolConfig(), conf.getDatabaseIp());
    }

    public JedisPool getPool() {
        return pool;
    }

    public static String createUsersIdKey(String guildId) {
        return "set:" + guildId + ":users";
    }

    public static String createUserIdValue(String userId) {
        return "user:id:" + userId;
    }

    public static String createUserKey(String guildId, String userId) {
        return "hset:" + guildId + ":users:id:" + userId;
    }

    public static Map<String, String> createUsersValue(String xp, String level, String leaderboard) {
        Map<String, String> map = new HashMap<>();
        map.put(XP_HASH, xp);
        map.put(LEVEL_HASH, level);
        map.put(LEADERBOARD_HASH, leaderboard);

        return map;
    }
}
