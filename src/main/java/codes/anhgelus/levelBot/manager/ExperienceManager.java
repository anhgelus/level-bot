package codes.anhgelus.levelBot.manager;

public class ExperienceManager {

    public static int experienceCalculator(String message) {
        int length = message.length();
        long chars = message.chars().distinct().count();

        return Math.round(experienceFormula(length, chars));
    }

    private static float experienceFormula(int length, long chars) {
        // f(x)=((0.025 x^(1.25))/(50^(-0.5)))+1
        float result = (float) (0.025f * Math.pow(length, 1.25));
        result = (float) (result / Math.pow(chars, -0.5));
        return result++;
    }

}
