package codes.anhgelus.levelBot.manager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ChannelManager {

    public static boolean checkValidChannel(String channelId, String guildId) {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = guildId + ":disabled-channel:" + channelId;

        try (Jedis jedis = pool.getResource()) {
            final String result = jedis.get(key);

            if (result == null) {
                pool.close();
                return true;
            }
        }
        pool.close();
        return false;
    }

    public static void addDisabledChannel(String channelId, String guildId) {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = guildId + ":disabled-channel:" + channelId;

        try (Jedis jedis = pool.getResource()) {
            jedis.set(key, "true");
        }
        pool.close();
    }

}
