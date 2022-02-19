package codes.anhgelus.levelBot.manager;

public class LevelManager {

    public static int getLevel(String userId, String guildId) {
        final int xp = ExperienceManager.getExperience(userId, guildId);
        final long lvl = Math.round(Math.floor(levelFormula(xp)));
        if (lvl > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.toIntExact(lvl);
    }

    public static int getLevelXpTotal(int lvl) {
        /*
        * f(x) = lvl
        * f(x) = 0.1 * x^0.5
        * lvl = 0.1 * x^0.5
        * lvl/0.1 = x^0.5
        * Math.sqrt(lvl/0.1) = x
         */
        long xp = Math.round(Math.pow((lvl / 0.1), 0.5*4));
        if (xp > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.toIntExact(xp);
    }

    public static int getLevelXpTotal(String lvl) {
        int realLvl = 0;
        try {
            realLvl = Integer.parseInt(lvl);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        long xp = Math.round(Math.pow((realLvl / 0.1), 0.5*4));
        if (xp > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.toIntExact(xp);
    }

    private static double levelFormula(int xp) {
        // f(x)=0.1*x^(0.5)
        return 0.1 * Math.pow(xp, 0.5);
    }

}
