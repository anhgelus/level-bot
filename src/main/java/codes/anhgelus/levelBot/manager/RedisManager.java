package codes.anhgelus.levelBot.manager;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private final JedisPool pool;

    public RedisManager() {
        ConfigManager conf = new ConfigManager("config.yml");

        this.pool = new JedisPool(new JedisPoolConfig(), conf.getDatabaseIp());
    }

    public JedisPool getPool() {
        return pool;
    }
}
