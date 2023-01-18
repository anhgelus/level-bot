package codes.anhgelus.levelBot.listeners;

import codes.anhgelus.levelBot.manager.VoiceManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Timestamp;

public class VoiceStateListener extends ListenerAdapter {

    private final JDA api;

    public VoiceStateListener(JDA api) {
        this.api = api;
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (event.getMember().getUser().isBot()) return;

        final VoiceManager voiceManager = new VoiceManager(event.getGuild().getId(), event.getMember().getId());
        voiceManager.connection(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getMember().getUser().isBot()) return;

        final VoiceManager voiceManager = new VoiceManager(event.getGuild().getId(), event.getMember().getId());
        voiceManager.disconnection(new Timestamp(System.currentTimeMillis()), event, this.api);
    }
}
