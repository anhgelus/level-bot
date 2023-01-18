package codes.anhgelus.levelBot.manager;

import java.util.Map;

public class StatusManager {
    public static String parseStatus(ConfigManager configManager) {
        final String status = configManager.getStatus();
        final String author = configManager.getAuthor();
        final String version = configManager.getVersion();

        return status.replace("{author}", author).replace("{version}", version);
    }
}
