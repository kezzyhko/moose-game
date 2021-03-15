package name.kezzyhlo.moose_game.players;


import name.kezzyhlo.moose_game.Random;

import java.util.ArrayList;
import java.util.List;

/**
 * This is greedy strategy, which chooses the field with the best X value at the current round.
 * If there are two or more fields with the best X value, then it will select randomly between them.
 */
public class BestFieldPlayer extends Player {

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        int[] x = {-1, xA, xB, xC};
        int maxValue = xA;
        List<Integer> maxIndexes = new ArrayList<>();
        maxIndexes.add(1);
        for (int i = 2; i <= x.length-1; i++) {
            if (x[i] > maxValue) {
                maxValue = x[i];
                maxIndexes.clear();
            }
            if (x[i] >= maxValue) {
                maxIndexes.add(i);
            }
        }
        return Random.randomFromList(maxIndexes);
    }

}
