package name.kezzyhlo.moose_game.players;


import name.kezzyhlo.moose_game.Random;

import java.util.Arrays;
import java.util.List;

/**
 * This strategy selects randomly each round between {@link CopycatPlayer} and {@link BestFieldPlayer} tactics.
 */
public class MixedBestCopyPlayer extends Player {

    private List<Player> tactics = Arrays.asList(
            new CopycatPlayer(),
            new BestFieldPlayer()
    );

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        return Random.randomFromList(tactics).move(opponentLastMove, xA, xB, xC);
    }

}
