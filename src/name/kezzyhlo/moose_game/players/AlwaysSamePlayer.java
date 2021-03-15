package name.kezzyhlo.moose_game.players;


import name.kezzyhlo.moose_game.Random;

/**
 * This strategy selects random move in the first round, and then continues selecting it each time.
 */
public class AlwaysSamePlayer extends Player {

    /**
     * This variable holds chosen move
     */
    private int move;

    /**
     * Chooses move that will be played all times
     */
    @Override
    public void reset() {
        move = Random.randomMove();
    }

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        return move;
    }

}