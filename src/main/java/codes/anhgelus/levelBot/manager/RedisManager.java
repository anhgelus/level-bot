package codes.anhgelus.levelBot.manager;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public JedisPool getPool() {
        return pool;
    }
}