package codes.anhgelus.levelBot.manager;

import java.util.Map;

public class StatusManager extends ConfigManager {

    public StatusManager(String name) {
        super(name);
    }

    public String parseStatus() {
        final Map<String, Object> conf = getConfig();
        final String status = getStatus();
        final String author = getAuthor();
        final String version = getVersion();

        return status.replace("{author}", author).replace("{version}", version);
    }
}
