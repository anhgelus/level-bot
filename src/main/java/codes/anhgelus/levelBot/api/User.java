package codes.anhgelus.levelBot.api;

import codes.anhgelus.levelBot.orm.database.Database;
import codes.anhgelus.levelBot.orm.entity.*;

@Entity(name = "users")
public class User extends OrmEntity {

    @Update
    @Column(name = "id", type = "SERIAL")
    @NotUpdate public long id;

    @Update
    @Column(name = "discord_id", type = "INTEGER")
    @NotUpdate public long discordId;

    public User(Database database) {
        super(database);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        onUpdateField("id");
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
        onUpdateField("discordId");
    }
}
