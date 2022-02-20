package codes.anhgelus.levelBot.commands;

import codes.anhgelus.levelBot.manager.ChannelManager;
import codes.anhgelus.levelBot.manager.RedisManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.awt.*;

public class SetupCommand {

    private final MessageReceivedEvent event;
    private final String[] args;
    private final RedisManager redisManager;

    public SetupCommand(MessageReceivedEvent event) {
        this.event = event;
        this.args = event.getMessage().getContentRaw().split(" ");
        this.redisManager = new RedisManager();

        final MessageChannel channel = event.getChannel();

        if (!event.getGuild().getMemberById(event.getAuthor().getId()).hasPermission(Permission.MANAGE_PERMISSIONS)) {
            this.event.getChannel().sendMessage("You don't have the permission to do this, sad!").queue();
            return;
        }

        final EmbedBuilder help = new EmbedBuilder()
                .setTitle("Setup Command")
                .setColor(Color.RED)
                .setDescription("Help for the setup command:\n> `grade-level {level} {grade id}` - setup a grade for the player who reached this level" +
                        "\n> `grade-leaderboard {place in the leaderboard} {grade id}` - setup a grade for the player who reached this place in the leaderboard" +
                        "\n> `disable-channel {channel id}` - disable a channel");

        /*
        * Args 0 = !setup
        * Args 1 = type
         */
        if (this.args.length > 1) {
            if (this.args[1].equals("grade-level") && (this.args.length == 4)) {
                gradeSetup("grade-level");
            } else if (this.args[1].equals("grade-leaderboard") && (this.args.length == 4)) {
                gradeSetup("grade-leaderboard");
            } else if (this.args[1].equals("disable-channel") && (this.args.length == 3)) {
                disableChannel();
            } else {
                channel.sendMessageEmbeds(help.build()).queue();
            }
            return;
        }

        channel.sendMessageEmbeds(help.build()).queue();
    }

    private void gradeSetup(String type) {
        /*
         * Args 2 = lvl/rank
         * Args 3 = id role
         */
        final JedisPool pool = this.redisManager.getPool();

        int lvl = 0;
        try {
            lvl = Integer.parseInt(this.args[2]);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.event.getChannel().sendMessage("This is not a valid level, sad!").queue();
            return;
        }

        long gradeId = 0;
        try {
            gradeId = Long.parseLong(this.args[3]);
            final Role role = this.event.getGuild().getRoleById(gradeId);
            if (role == null) {
                this.event.getChannel().sendMessage("This role doesn't exist, sad!").queue();
                return;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.event.getChannel().sendMessage("This is not a valid id, sad!").queue();
            return;
        }

        final String key = this.event.getGuild().getId() + ":" + type + ":" + lvl;

        try (Jedis jedis = pool.getResource()) {
            jedis.set(key, String.valueOf(gradeId));
        }
        pool.close();

        this.event.getChannel().sendMessage("Role " + this.event.getGuild().getRoleById(gradeId).getName() + " has been added to the level " + lvl).queue();
    }

    private void disableChannel() {
        try {
            this.event.getGuild().getGuildChannelById(this.args[2]);
            ChannelManager.addDisabledChannel(this.args[2], this.event.getGuild().getId());
        } catch (Exception e) {
            this.event.getChannel().sendMessage(e.getMessage()).queue();
        }
    }
}
