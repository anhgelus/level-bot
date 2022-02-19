package codes.anhgelus.levelBot.manager;

import codes.anhgelus.levelBot.LevelBot;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    private final JedisPool pool;

    public RedisManager() {
        ConfigManager conf = new ConfigManager(LevelBot.CONF_FILE_NAME);

        this.pool = new JedisPool(new JedisPoolConfig(), conf.getDatabaseIp());
    }

    public JedisPool getPool() {
        return pool;
    }
}
