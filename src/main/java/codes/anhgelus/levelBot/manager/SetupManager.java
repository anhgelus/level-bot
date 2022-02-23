package codes.anhgelus.levelBot.manager;

import codes.anhgelus.levelBot.commands.SetupCommand;
import redis.clients.jedis.Jedis;

public class SetupManager {

    private final Jedis jedis;

    public SetupManager(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isGradeLevel(String key, int level) {
        return this.getLevelOfGrade(key, level).length() != 0;
    }

    public String getGradeList(String key) {
        return this.jedis.hget(key, RedisManager.GRADE_LEVEL_HASH);
    }

    public String getLevelOfGrade(String key, int level) {
        final String gradeList = this.getGradeList(key);
        if (gradeList == null) return "";

        final String[] grades = gradeList.split(SetupCommand.SEPARATOR);

        for (final String i : grades) {
            final String[] n = i.split(RedisManager.SPLIT);
            if (n[0].equals(String.valueOf(level)) && n.length == 2) {
                return n[1];
            }
        }

        return "";
    }

}
