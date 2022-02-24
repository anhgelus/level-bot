package codes.anhgelus.levelBot.manager;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigManager {

    protected final String configFile;

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

    public String getAuthor() { return (String) this.getConfig().get("author"); }

    public String getVersion() { return (String) this.getConfig().get("version"); }

    public String getStatus() { return (String) this.getConfig().get("status"); }

}
