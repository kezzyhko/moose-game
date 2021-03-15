package name.kezzyhlo.moose_game;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains functions for getting random integers, random from a list of choices, etc.
 */
@SuppressWarnings("WeakerAccess")
public final class Random {

    /**
     * No instances needed for this class
     */
    private Random() {}

    /**
     * Returns random integer between {@code min} and {@code max}
     * @param min Lower bound (including {@code min} itself)
     * @param max Upper bound (including {@code max} itself)
     * @return Random integer between {@code min} and {@code max}
     */
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }

    /**
     * Returns random move
     *
     * @return Random move
     */
    public static int randomMove() {
        return randomInt(1, 3);
    }

    /**
     * Returns random move, excluding {@code m}
     *
     * @param m Move to exclude
     * @return Random move, excluding {@code m}
     */
    public static int randomMoveExcluding(int m) {
        return (randomInt(1, 2) + m - 1) % 3 + 1;
    }

    /**
     * Returns random value from the array {@code list}
     *
     * @param list Array of values to select from
     * @return Random value from the {@code list} array
     */
    public static <T> T randomFromList(List<T> list) {
        return list.get(randomInt(0, list.size() - 1));
    }

}
