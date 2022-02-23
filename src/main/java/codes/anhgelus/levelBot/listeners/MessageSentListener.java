package codes.anhgelus.levelBot.listeners;

import codes.anhgelus.levelBot.LevelBot;
import codes.anhgelus.levelBot.commands.SetupCommand;
import codes.anhgelus.levelBot.manager.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class MessageSentListener extends ListenerAdapter {

    private final JDA api;

    public MessageSentListener(JDA api) {
        this.api = api;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        final ConfigManager conf = new ConfigManager(LevelBot.CONF_FILE_NAME);
        final String prefix = conf.getPrefix();

        final ChannelManager channelManager = new ChannelManager(event);

        final Message message = event.getMessage();
        final String content = message.getContentRaw();

        if (content.startsWith(prefix)) {
            forBotMessage(event, prefix);
            return;
        }

        if (!channelManager.checkValidChannel(event.getChannel().getId())) return;

        final ExperienceManager experienceManager = new ExperienceManager(event.getGuild().getId(), event.getAuthor().getId());

        int xp = ExperienceManager.experienceCalculator(content);
        experienceManager.addExperience(xp, event, this.api);
    }

    private void forBotMessage(MessageReceivedEvent event, String prefix) {
        final Message message = event.getMessage();
        final MessageChannel channel = event.getChannel();
        final String content = message.getContentRaw();

        final LevelManager levelManager = new LevelManager(event.getGuild().getId(), event.getAuthor().getId());
        final ExperienceManager experienceManager = new ExperienceManager(event.getGuild().getId(), event.getAuthor().getId());

        if (content.equals(prefix + "xp")) {
            int xp = experienceManager.getExperience();
            channel.sendMessage("You have " + xp + " xp points!").queue();
        } else if (content.equals(prefix + "level")) {
            int lvl = levelManager.getLevel();
            channel.sendMessage("Your level is " + lvl + "!").queue();
        } else if (content.startsWith(prefix + "xpto") && (content.split(" ").length == 2)) {
            final String[] args = content.split(" ");
            int xp = LevelManager.getLevelXpTotal(args[1]);
            if (xp == -2) {
                channel.sendMessage("I was beta-tested noobz :p").queue();
                return;
            }
            channel.sendMessage("You need " + xp + " xp points to have access to the level " + args[1] + "!").queue();
        } else if (content.startsWith(prefix + "setup")) {
            final SetupCommand setupCommand = new SetupCommand(event);
        } else if (content.startsWith(prefix + "leaderboard")) {
            channel.sendMessage("The leaderboard is currently in development. Check the github for more information.").queue();
        } else {
            final EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("Help")
                    .setDescription("Commands:\n" + "> `xp` - see your xp\n" + "> `level` - see your level\n" +
                            "> `xpto {level}` - see the total xp needed to reach that level\n" +
                            "> `leaderboard` - see the leaderboard\n" +
                            "> `setup` - setup the bot (use it to see its own help page)")
                    .setColor(Color.ORANGE);
            channel.sendMessageEmbeds(eb.build()).queue();
        }
    }
}
