package codes.anhgelus.levelBot;

import codes.anhgelus.levelBot.listeners.HelpListener;
import codes.anhgelus.levelBot.manager.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class LevelBot {

    public static void main(String[] args) throws Exception{

        final ConfigManager conf = new ConfigManager("config.yml");

        final String token = (String) conf.getConfig().get("token");

        JDA api = JDABuilder.createDefault(token)
                .addEventListeners(new HelpListener())
                .build().awaitReady();

    }

}
