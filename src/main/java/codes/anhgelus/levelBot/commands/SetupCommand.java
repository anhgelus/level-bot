package codes.anhgelus.levelBot.commands;

import codes.anhgelus.levelBot.manager.ChannelManager;
import codes.anhgelus.levelBot.manager.RedisManager;
import codes.anhgelus.levelBot.manager.SetupManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SetupCommand {

    private final MessageReceivedEvent event;

    private final String[] args;

    private final MessageChannel channel;

    private final RedisManager redisManager;
    private final ChannelManager channelManager;

    public SetupCommand(MessageReceivedEvent event) {
        this.event = event;
        this.args = event.getMessage().getContentRaw().split(" ");
        this.channel = event.getChannel();
        this.redisManager = new RedisManager();
        this.channelManager = new ChannelManager(event);

        try {
            if (!event.getGuild().getMemberById(event.getAuthor().getId()).hasPermission(Permission.MANAGE_SERVER)) {
                this.channel.sendMessage("You don't have the permission to do this, sad!").queue();
                return;
            }
        } catch (Exception e) {
            System.out.println("Common Setup Error: " + e.getMessage());
            this.channel.sendMessage("You don't have the permission to do this, sad!").queue();
            return;
        }

        final EmbedBuilder help = new EmbedBuilder()
                .setTitle("Setup Command")
                .setColor(Color.RED)
                .setDescription("Help for the setup command:\n> `grade-level add|remove {level} {grade id}` - setup a grade for the player who reached this level" +
                        "\n> `grade-leaderboard add|remove {place in the leaderboard} {grade id}` - setup a grade for the player who reached this place in the leaderboard" +
                        "\n> `disable-channel add|remove {channel id}` - disable a channel" +
                        "\n> `default-channel {channel id}` - set the default channel" +
                        "\n> `see` - see the actual config");

        /*
        * Args 0 = !setup
        * Args 1 = type
         */
        if (this.args.length > 1) {
            if (this.args[1].equals("grade-level") && (this.args.length == 5)) {
                gradeSetup(RedisManager.GRADE_LEVEL_HASH);
            } else if (this.args[1].equals("grade-leaderboard") && (this.args.length == 5)) {
                gradeSetup(RedisManager.GRADE_LEADERBOARD_HASH);
            } else if (this.args[1].equals("disable-channel") && (this.args.length == 4)) {
                disableChannel();
            } else if (this.args[1].equals("default-channel") && (this.args.length == 3)) {
                defaultChannel();
            } else if (this.args[1].equals("see") && (this.args.length == 2)) {
                seeConfig();
            } else {
                this.channel.sendMessageEmbeds(help.build()).queue();
            }
            return;
        }

        this.channel.sendMessageEmbeds(help.build()).queue();
    }

    private void gradeSetup(String type) {
        /*
         * Args 2 = lvl/rank
         * Args 3 = id role
         */
        final JedisPool pool = this.redisManager.getPool();

        int lvl = 0;
        try {
            lvl = Integer.parseInt(this.args[3]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.channel.sendMessage("This is not a valid level, sad!").queue();
            return;
        }

        long gradeId = 0;
        try {
            gradeId = Long.parseLong(this.args[4]);
            final Role role = this.event.getGuild().getRoleById(gradeId);
            if (role == null) {
                this.channel.sendMessage("This role doesn't exist, sad!").queue();
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.channel.sendMessage("This is not a valid id, sad!").queue();
            return;
        }

        final String key = RedisManager.setupKey(this.event.getGuild().getId());

        try (Jedis jedis = pool.getResource()) {
            final SetupManager sm = new SetupManager(jedis);

            final String typeOfUsage = this.args[2];
            System.out.println(typeOfUsage);

            switch (typeOfUsage) {
                case "add" -> {
                    sm.setGrade(key, type, new String[]{String.valueOf(lvl), String.valueOf(gradeId)});
                    this.channel.sendMessage("Role " + this.event.getGuild().getRoleById(gradeId).getName() +
                            " has been added to the level " + lvl).queue();
                }
                case "remove" -> {
                    sm.removeGrade(key, type, new String[]{String.valueOf(lvl), String.valueOf(gradeId)});
                    this.channel.sendMessage("Role " + this.event.getGuild().getRoleById(gradeId).getName() +
                            " has been removed to the level " + lvl).queue();
                }
            }
        }
        pool.close();
    }

    private void disableChannel() {
        final String channelId = this.args[3];
        this.event.getGuild().getGuildChannelById(channelId);
        final JedisPool pool = this.redisManager.getPool();

        try (Jedis jedis = pool.getResource()) {
            final SetupManager sm = new SetupManager(jedis);
            switch (this.args[2]) {
                case "add" -> {
                    try {
                        sm.setChannel(RedisManager.setupKey(this.event.getGuild().getId()), channelId);

                        this.channel.sendMessage("The channel <#" + channelId + "> has been disabled!").queue();
                    } catch (Exception e) {
                        this.channel.sendMessage(e.getMessage()).queue();
                    }
                }
                case "remove" -> {
                    try {
                        sm.removeChannel(RedisManager.setupKey(this.event.getGuild().getId()), channelId);

                        this.channel.sendMessage("The channel <#" + channelId + "> has been enabled!").queue();
                    } catch (Exception e) {
                        this.channel.sendMessage(e.getMessage()).queue();
                    }
                }
            }
        }
        pool.close();
    }

    private void defaultChannel() {
        try {
            final String channelId = this.args[2];
            this.event.getGuild().getGuildChannelById(channelId);
            this.channelManager.setDefaultChannel(channelId);
            this.channel.sendMessage("The channel <#" + channelId + "> has been selected!").queue();
        } catch (Exception e) {
            this.channel.sendMessage(e.getMessage()).queue();
        }
    }

    private void seeConfig() {
        final JedisPool pool = this.redisManager.getPool();

        Map<String, String> result = new HashMap<>();

        try (Jedis jedis = pool.getResource()) {
            final String guildId = this.event.getGuild().getId();

            final String defaultChannel = jedis.hget(RedisManager.setupKey(guildId), RedisManager.DEFAULT_CHANNEL_HASH);
            if (defaultChannel != null) result.put(RedisManager.DEFAULT_CHANNEL_HASH, defaultChannel);

            final String disabledChannel = jedis.hget(RedisManager.setupKey(guildId), RedisManager.DISABLED_CHANNEL_HASH);
            if (disabledChannel != null) result.put(RedisManager.DISABLED_CHANNEL_HASH, disabledChannel);

            final String gradeLevel = jedis.hget(RedisManager.setupKey(guildId), RedisManager.GRADE_LEVEL_HASH);
            if (gradeLevel != null) result.put(RedisManager.GRADE_LEVEL_HASH, gradeLevel);

            final String gradeLeaderboard = jedis.hget(RedisManager.setupKey(guildId), RedisManager.GRADE_LEADERBOARD_HASH);
            if (gradeLeaderboard != null) result.put(RedisManager.GRADE_LEADERBOARD_HASH, gradeLeaderboard);
        }
        pool.close();

        final String defaultChannel = result.get(RedisManager.DEFAULT_CHANNEL_HASH);
        final String disabledChannel = result.get(RedisManager.DISABLED_CHANNEL_HASH);
        final String gradeLevel = result.get(RedisManager.GRADE_LEVEL_HASH);
        final String gradeLeaderboard = result.get(RedisManager.GRADE_LEADERBOARD_HASH);

        if (defaultChannel == null && disabledChannel == null && gradeLevel == null && gradeLeaderboard == null) {
            this.channel.sendMessage("You never set the config!").queue();
            return;
        }

        final EmbedBuilder config = new EmbedBuilder()
                .setTitle("Actual Config")
                .setColor(Color.RED);

        if (defaultChannel != null) config.addField("Default Channel", "<#" + defaultChannel + ">", false);
        if (disabledChannel != null) config.addField("Disabled Channel", SetupManager.parseChannel(disabledChannel), false);
        if (gradeLevel != null) config.addField("Grade Level", SetupManager.parseGradeLevel(gradeLevel), false);
        if (gradeLeaderboard != null) config.addField("Grade Leaderboard", SetupManager.parseGradeLevel(gradeLeaderboard), false);

        this.channel.sendMessageEmbeds(config.build()).queue();
    }
}
