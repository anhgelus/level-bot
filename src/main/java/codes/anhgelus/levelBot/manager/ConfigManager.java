package codes.anhgelus.levelBot.manager;

import org.jetbrains.annotations.NotNull;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    protected final String configFile;
    public final Path configPath;
    public final TomlParseResult config;

    public ConfigManager(@NotNull String name) throws IOException {
        configFile = name;
        configPath = Path.of("./"+configFile);
        generate();
        config = Toml.parse(configPath);
        config.errors().forEach(error -> System.err.println(error.toString()));
    }

    private void generate() {
        var is = this.getClass().getClassLoader().getResourceAsStream(this.configFile);
        try {
            if (!configPath.toFile().createNewFile()) {
                assert is != null;
                Files.write(configPath, is.readAllBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken() {
        return config.getString("token");
    }

    public String getVersion() {
        return config.getString("information.version");
    }

    public String getAuthor() {
        return config.getString("information.author");
    }

    public String getPrefix() {
        return config.getString("information.prefix");
    }

    public String getStatus() {
        return config.getString("information.status");
    }

    public String getSqlType() {
        return config.getString("database.sql.type");
    }

    public String getSqlHost() {
        return config.getString("database.sql.host");
    }

    public long getSqlPort() {
        return config.getLong("database.sql.port");
    }

    public String getSqlDatabase() {
        return config.getString("database.sql.database");
    }

    public String getSqlUser() {
        return config.getString("database.sql.user");
    }

    public String getSqlPassword() {
        return config.getString("database.sql.password");
    }

    public String getRedisHost() {
        return config.getString("database.redis.host");
    }

    public long getRedisPort() {
        return config.getLong("database.redis.port");
    }

}
