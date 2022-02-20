package codes.anhgelus.levelBot.listeners;

import codes.anhgelus.levelBot.LevelBot;
import codes.anhgelus.levelBot.commands.SetupCommand;
import codes.anhgelus.levelBot.manager.ChannelManager;
import codes.anhgelus.levelBot.manager.ConfigManager;
import codes.anhgelus.levelBot.manager.ExperienceManager;
import codes.anhgelus.levelBot.manager.LevelManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageSentListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final ConfigManager conf = new ConfigManager(LevelBot.CONF_FILE_NAME);
        final String prefix = conf.getPrefix();

        final ExperienceManager experienceManager = new ExperienceManager(event);
        final ChannelManager channelManager = new ChannelManager(event);

        if (event.getAuthor().isBot()) return;

        final Message message = event.getMessage();
        final String content = message.getContentRaw();

        if (content.startsWith(prefix)) {
            forBotMessage(event, prefix);
            return;
        }

        if (!channelManager.checkValidChannel(event.getChannel().getId())) return;

        int xp = ExperienceManager.experienceCalculator(content);
        experienceManager.addExperience(xp);
    }

    private void forBotMessage(MessageReceivedEvent event, String prefix) {
        final Message message = event.getMessage();
        final MessageChannel channel = event.getChannel();
        final String content = message.getContentRaw();

        final LevelManager levelManager = new LevelManager(event);
        final ExperienceManager experienceManager = new ExperienceManager(event);

        if (content.equals(prefix + "xp")) {
            int xp = experienceManager.getExperience();
            channel.sendMessage("You have " + xp + " xp points!").queue();
        } else if (content.equals(prefix + "level")) {
            int lvl = levelManager.getLevel();
            channel.sendMessage("Your level is " + lvl + "!").queue();
        } else if (content.startsWith(prefix + "xpto") && (content.split(" ").length == 2)) {
            final String[] args = content.split(" ");
            int xp = LevelManager.getLevelXpTotal(args[1]);
            channel.sendMessage("You need " + xp + " xp points to have access to the level " + args[1] + "!").queue();
        } else if (content.startsWith(prefix + "setup")) {
            final SetupCommand setupCommand = new SetupCommand(event);
        }
    }
}
