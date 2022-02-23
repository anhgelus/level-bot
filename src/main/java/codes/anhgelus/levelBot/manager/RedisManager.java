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
    public final static String CONNECTION_HASH = "voice-connection-joined";

    public final static String GRADE_LEVEL_HASH = "grade-level";
    public final static String GRADE_LEADERBOARD_HASH = "grade-leaderboard";
    public final static String DISABLED_CHANNEL_HASH = "disable-channel";
    public final static String DEFAULT_CHANNEL_HASH = "default-channel";

    public final static String HASH = "hset";
    public final static String SET = "set";
    public final static String SPLIT = ":";

    public RedisManager() {
        ConfigManager conf = new ConfigManager(LevelBot.CONF_FILE_NAME);

        this.pool = new JedisPool(new JedisPoolConfig(), conf.getDatabaseIp());
    }

    public JedisPool getPool() {
        return pool;
    }

    public static String createUsersIdKey(String guildId) {
        return SET + ":" + guildId + ":users";
    }

    public static String createUserIdValue(String userId) {
        return SET + ":" + "user:id:" + userId;
    }

    public static String createUserKey(String guildId, String userId) {
        return HASH + ":" + guildId + ":users:id:" + userId;
    }

    public static Map<String, String> createUserValue(String xp, String level, String leaderboard, String voiceConnectionJoined) {
        Map<String, String> map = new HashMap<>();
        if (xp.length() != 0) map.put(XP_HASH, xp);
        if (level.length() != 0) map.put(LEVEL_HASH, level);
        if (leaderboard.length() != 0) map.put(LEADERBOARD_HASH, leaderboard);
        if (voiceConnectionJoined.length() != 0) map.put(CONNECTION_HASH, voiceConnectionJoined);

        return map;
    }

    public static String setupKey(String guildId) {
        return HASH + ":" + guildId + ":" + ":setup";
    }

    public static Map<String, String> setupValue(String gradeLevel, String gradeLeaderboard, String disableChannel, String defaultChannel) {
        Map<String, String> map = new HashMap<>();
        if (gradeLevel.length() != 0) map.put(GRADE_LEVEL_HASH, gradeLevel);
        if (gradeLeaderboard.length() != 0) map.put(GRADE_LEADERBOARD_HASH, gradeLeaderboard);
        if (disableChannel.length() != 0) map.put(DISABLED_CHANNEL_HASH, disableChannel);
        if (defaultChannel.length() != 0) map.put(DEFAULT_CHANNEL_HASH, defaultChannel);

        return map;
    }
}
