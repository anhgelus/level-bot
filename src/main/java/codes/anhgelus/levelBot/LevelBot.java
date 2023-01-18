package codes.anhgelus.levelBot;

import codes.anhgelus.levelBot.listeners.MessageSentListener;
import codes.anhgelus.levelBot.listeners.VoiceStateListener;
import codes.anhgelus.levelBot.manager.ConfigManager;
import codes.anhgelus.levelBot.manager.StatusManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class LevelBot {

    public final static String CONF_FILE_NAME = "config.toml";

    public static void main(String[] args) throws Exception{
        final var conf = new ConfigManager(CONF_FILE_NAME);

        final var token = conf.getToken();

        final var api = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        api.awaitReady();
        api.addEventListener(new MessageSentListener(api));
        api.addEventListener(new VoiceStateListener(api));

        api.getPresence().setActivity(Activity.playing(StatusManager.parseStatus(conf)));
    }

}
