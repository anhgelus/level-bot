package codes.anhgelus.levelBot;

import codes.anhgelus.levelBot.listeners.MessageSentListener;
import codes.anhgelus.levelBot.manager.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class LevelBot {

    public final static String CONF_FILE_NAME = "config.yml";

    public static void main(String[] args) throws Exception{

        final ConfigManager conf = new ConfigManager(CONF_FILE_NAME);

        final String token = conf.getToken();

        JDA api = JDABuilder.createDefault(token)
                .addEventListeners(new MessageSentListener())
                .build().awaitReady();

    }

}
