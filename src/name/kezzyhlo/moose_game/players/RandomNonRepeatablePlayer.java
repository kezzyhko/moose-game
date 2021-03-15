package name.kezzyhlo.moose_game.players;


import name.kezzyhlo.moose_game.Random;

/**
 * This strategy chooses random field like {@link RandomPlayer},
 * but it will not choose the same field twice in a row
 */
public class RandomNonRepeatablePlayer extends Player {

    /**
     * This variable holds the previous move of this player.
     * If no move was made yet, it is {@code 0}
     */
    private int previousMove;

    /**
     * Resets {@link RandomNonRepeatablePlayer#previousMove} variable to {@code 0}
     */
    @Override
    public void reset() {
        previousMove = 0;
    }

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        if (previousMove == 0) return Random.randomMove();
        return Random.randomMoveExcluding(previousMove);
    }

}
