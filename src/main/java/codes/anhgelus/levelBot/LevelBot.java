package codes.anhgelus.levelBot;

import codes.anhgelus.levelBot.listeners.MessageSentListener;
import codes.anhgelus.levelBot.listeners.VoiceStateListener;
import codes.anhgelus.levelBot.manager.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class LevelBot {

    public final static String CONF_FILE_NAME = "config.yml";

    public static void main(String[] args) throws Exception{
        final ConfigManager conf = new ConfigManager(CONF_FILE_NAME);

        final String token = conf.getToken();
        final String version = conf.getVersion();
        final String author = conf.getAuthor();

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        JDA api = builder.build().awaitReady();
        api.addEventListener(new MessageSentListener(api));
        api.addEventListener(new VoiceStateListener(api));

        api.getPresence().setActivity(Activity.playing("Level Bot " + version + " by " + author));

    }

}
