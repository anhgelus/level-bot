package codes.anhgelus.levelBot;

import codes.anhgelus.levelBot.listeners.MessageSentListener;
import codes.anhgelus.levelBot.manager.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class LevelBot {

    public final static String CONF_FILE_NAME = "config.yml";

    public static void main(String[] args) throws Exception{
        final ConfigManager conf = new ConfigManager(CONF_FILE_NAME);

        final String token = conf.getToken();
        final String version = conf.getVersion();
        final String author = conf.getAuthor();



        JDA api = JDABuilder.createDefault(token)
                .addEventListeners(new MessageSentListener())
                .build().awaitReady();

        api.getPresence().setActivity(Activity.playing("Level Bot " + version + " by " + author));

    }

}
