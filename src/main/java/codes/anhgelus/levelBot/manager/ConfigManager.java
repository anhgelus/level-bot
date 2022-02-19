package codes.anhgelus.levelBot.manager;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigManager {

    private final String configFile;

    public ConfigManager(String name) {
        configFile = name;
    }

    protected Map<String, Object> getConfig() {
        Yaml yaml = new Yaml();

        final InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.configFile);
        return yaml.load(is);
    }

    public String getToken() {
        return (String) this.getConfig().get("token");
    }

    public String getDatabaseIp() {
        return (String) this.getConfig().get("database-ip");
    }

    public String getPrefix() {
        return (String) this.getConfig().get("prefix");
    }

}
