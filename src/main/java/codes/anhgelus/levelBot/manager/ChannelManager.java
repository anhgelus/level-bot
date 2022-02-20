package codes.anhgelus.levelBot.manager;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ChannelManager {

    private final MessageReceivedEvent event;

    private final String guildId;

    public ChannelManager(MessageReceivedEvent event) {
        this.event = event;
        this.guildId = this.event.getGuild().getId();
    }

    public boolean checkValidChannel(String channelId) {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = this.guildId + ":disabled-channel:" + channelId;

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

    public void addDisabledChannel(String channelId) {
        final RedisManager redisManager = new RedisManager();
        final JedisPool pool = redisManager.getPool();

        final String key = this.guildId + ":disabled-channel:" + channelId;

        try (Jedis jedis = pool.getResource()) {
            jedis.set(key, "true");
        }
        pool.close();
    }

}
