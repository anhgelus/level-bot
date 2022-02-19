package codes.anhgelus.levelBot.listeners;

import codes.anhgelus.levelBot.LevelBot;
import codes.anhgelus.levelBot.manager.ConfigManager;
import codes.anhgelus.levelBot.manager.ExperienceManager;
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

        if (content.startsWith(prefix + "xp")) {
            int xp = ExperienceManager.getExperience(event.getAuthor().getId(), event.getGuild().getId());
            channel.sendMessage("You have " + xp + " xp points!").queue();
        }
    }
}
