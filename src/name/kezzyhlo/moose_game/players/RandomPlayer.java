package name.kezzyhlo.moose_game.players;


import name.kezzyhlo.moose_game.Random;

/**
 * This strategy just chooses random field.
 */
public class RandomPlayer extends Player {

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        return Random.randomMove();
    }

}
