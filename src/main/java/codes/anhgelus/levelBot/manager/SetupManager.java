package codes.anhgelus.levelBot.manager;

import redis.clients.jedis.Jedis;

public class SetupManager {

    private final Jedis jedis;

    public static final String SEPARATOR = ",";

    public SetupManager(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isGradeLevel(String key, int level) {
        return this.getGradeLevel(key, level).length() != 0;
    }

    public String getGradeList(String key) {
        return this.jedis.hget(key, RedisManager.GRADE_LEVEL_HASH);
    }

    public String getGradeLevel(String key, int level) {
        final String gradeList = this.getGradeList(key);
        if (gradeList == null) return "";

        final String[] grades = gradeList.split(SEPARATOR);

        for (final String i : grades) {
            final String[] n = i.split(RedisManager.SPLIT);
            if (n[0].equals(String.valueOf(level)) && n.length == 2) {
                return n[1];
            }
        }

        return "";
    }

    public void setGrade(String key, String type, String[] info) {
        switch (type) {
            case RedisManager.GRADE_LEVEL_HASH: {
                final String oldGrade = jedis.hget(key, RedisManager.GRADE_LEVEL_HASH);
                if (oldGrade == null) {
                    jedis.hset(key, RedisManager.setupValue(info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", "", ""));
                    return;
                }
                jedis.hset(key, RedisManager.setupValue(oldGrade + info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", "", ""));
            }
            case RedisManager.GRADE_LEADERBOARD_HASH: {
                final String oldGrade = jedis.hget(key, RedisManager.GRADE_LEADERBOARD_HASH);
                if (oldGrade == null) {
                    jedis.hset(key, RedisManager.setupValue("", info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", ""));
                    return;
                }
                jedis.hset(key, RedisManager.setupValue("", oldGrade + info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", ""));
            }
        }
    }

    public void setChannel(String key, String info) {
        final String oldChannel = jedis.hget(key, RedisManager.DISABLED_CHANNEL_HASH);
        if (oldChannel == null) {
            jedis.hset(key, RedisManager.setupValue("", "", info + SEPARATOR, ""));
            return;
        }
        jedis.hset(key, RedisManager.setupValue("", "", oldChannel + info + SEPARATOR, ""));
    }


    public static String parseGradeLevel(String grades) {
        // level:roleId,
        final String[] grade = grades.split(",");
        StringBuilder toReturn = new StringBuilder();
        for (String i : grade) {
            final String[] n = i.split(":");
            if (toReturn.length() == 0) {
                toReturn.append("Level ").append(n[0]).append(" - <@&").append(n[1]).append(">");
            } else {
                toReturn.append("\nLevel ").append(n[0]).append(" - <@&").append(n[1]).append(">");
            }
        }
        return toReturn.toString();
    }

    public static String parseChannel(String channels) {
        // id,
        final String[] grade = channels.split(",");
        StringBuilder toReturn = new StringBuilder();
        for (String i : grade) {
            if (toReturn.length() == 0) {
                toReturn.append("<#").append(i).append(">");
            } else {
                toReturn.append("\n<#").append(i).append(">");
            }
        }
        return toReturn.toString();
    }
}
