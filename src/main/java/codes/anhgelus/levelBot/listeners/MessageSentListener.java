package codes.anhgelus.levelBot.listeners;

import codes.anhgelus.levelBot.LevelBot;
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

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.startsWith(prefix)) {
            forBotMessage(event, prefix);
            return;
        }
        int xp = ExperienceManager.experienceCalculator(content);
        ExperienceManager.addExperience(event.getAuthor().getId(), event.getGuild().getId(), xp);
    }

    private void forBotMessage(MessageReceivedEvent event, String prefix) {
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String content = message.getContentRaw();

        if (content.equals(prefix + "xp")) {
            int xp = ExperienceManager.getExperience(event.getAuthor().getId(), event.getGuild().getId());
            channel.sendMessage("You have " + xp + " xp points!").queue();
        } else if (content.equals(prefix + "level")) {
            int lvl = LevelManager.getLevel(event.getAuthor().getId(), event.getGuild().getId());
            channel.sendMessage("Your level is " + lvl + "!").queue();
        } else if (content.startsWith(prefix + "xpto") && (content.split(" ").length == 2)) {
            final String[] args = content.split(" ");
            int xp = LevelManager.getLevelXpTotal(args[1]);
            channel.sendMessage("You need " + xp + " xp points to have access to the level " + args[1] + "!").queue();
        }
    }
}
