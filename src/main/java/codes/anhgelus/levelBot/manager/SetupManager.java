package codes.anhgelus.levelBot.manager;

import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            case RedisManager.GRADE_LEVEL_HASH -> {
                final String oldGrade = this.jedis.hget(key, RedisManager.GRADE_LEVEL_HASH);
                if (oldGrade == null) {
                    this.jedis.hset(key, RedisManager.setupValue(info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", "", ""));
                    return;
                }
                this.jedis.hset(key, RedisManager.setupValue(oldGrade + info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", "", ""));
            }
            case RedisManager.GRADE_LEADERBOARD_HASH -> {
                final String oldGrade = this.jedis.hget(key, RedisManager.GRADE_LEADERBOARD_HASH);
                if (oldGrade == null) {
                    this.jedis.hset(key, RedisManager.setupValue("", info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", ""));
                    return;
                }
                this.jedis.hset(key, RedisManager.setupValue("", oldGrade + info[0] + RedisManager.SPLIT + info[1] + SEPARATOR, "", ""));
            }
        }
    }

    public void removeGrade(String key, String type, String[] info) {
        if (!Objects.equals(type, RedisManager.GRADE_LEVEL_HASH) && !Objects.equals(type, RedisManager.GRADE_LEADERBOARD_HASH)) {
            return;
        }

        final String oldGrade = this.jedis.hget(key, type);

        if (oldGrade == null) {
            return;
        }

        final String[] oldGrades = oldGrade.split(",");
        StringBuilder newGradeB = new StringBuilder();
        for (String i : oldGrades) {
            System.out.println(Arrays.toString(i.split(RedisManager.SPLIT)));
            System.out.println(Arrays.toString(info));
            if (!Arrays.equals(i.split(RedisManager.SPLIT), info)) {
                newGradeB.append(i + SEPARATOR);
            }
        }

        String newGrade = newGradeB.toString();
        Map<String, String> map = new HashMap<>();
        try {
            switch (type) {
                case RedisManager.GRADE_LEVEL_HASH -> {
                    map.put(RedisManager.GRADE_LEVEL_HASH, newGrade);
                    this.jedis.hset(key, map);
                }
                case RedisManager.GRADE_LEADERBOARD_HASH -> {
                    map.put(RedisManager.GRADE_LEADERBOARD_HASH, newGrade);
                    this.jedis.hset(key, map);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void setChannel(String key, String info) {
        final String oldChannel = this.jedis.hget(key, RedisManager.DISABLED_CHANNEL_HASH);
        if (oldChannel == null) {
            this.jedis.hset(key, RedisManager.setupValue("", "", info + SEPARATOR, ""));
            return;
        }
        this.jedis.hset(key, RedisManager.setupValue("", "", oldChannel + info + SEPARATOR, ""));
    }

    public void removeChannel(String key, String info) {
        final String oldChannel = this.jedis.hget(key, RedisManager.DISABLED_CHANNEL_HASH);

        if (oldChannel == null) {
            return;
        }

        final String[] oldChannels = oldChannel.split(SEPARATOR);

        StringBuilder newChannel = new StringBuilder();
        for (String i : oldChannels) {
            if (!i.equals(info)) {
                newChannel.append(i + SEPARATOR);
            }
        }

        if (newChannel.toString().length() == 0) {
            System.out.println("Error when removing the channel");
            return;
        }

        this.jedis.hset(key, RedisManager.setupValue("", "", newChannel.toString(), ""));
    }


    public static String parseGradeLevel(String grades) {
        // level:roleId,
        System.out.println(grades);
        if (grades.length() == 0) {
            return "No valid grade was set :(";
        }
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
