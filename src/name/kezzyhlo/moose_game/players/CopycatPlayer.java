package name.kezzyhlo.moose_game.players;


import name.kezzyhlo.moose_game.Random;

/**
 * This strategy selects random move in the first round, and then it chooses the same move,
 * which the opponents selected in the previous round.
 * If such a move will lead to the payoff of 0, then it selects the random move from others.
 * If one of the other moves also leads to 0, this strategy will not choose it.
 */
public class CopycatPlayer extends Player {

    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {
        int[] x = {-1, xA, xB, xC};
        if (opponentLastMove <= 0 || opponentLastMove > 3) {
            return Random.randomMove();
        } else if (x[opponentLastMove] != 0) {
            return opponentLastMove;
        } else {
            int move1 = (opponentLastMove != 1) ? 1 : 2;
            int move2 = 1 + 2 + 3 - opponentLastMove - move1;
            if (x[move1] == 0) {
                return move2;
            } else if (x[move2] == 0) {
                return move1;
            } else {
                return Random.randomMoveExcluding(opponentLastMove);
            }
        }
    }

}
