package name.kezzyhlo.moose_game.players;


/**
 * Abstract class with basic things defined
 */
public abstract class Player {

    /**
     * This method is called to reset the agent before the match
     * with another player containing several rounds
     */
    public void reset() {}

    /**
     * This method returns the move of the player based on
     * the last move of the opponent and X values of all fields.
     * Initially, X for all fields is equal to 1 and last opponent
     * move is equal to 0
     *
     * @param opponentLastMove the last move of the opponent
     *                         varies from 0 to 3
     *                         (0 – if this is the first move)
     * @param xA               the argument X for a field A
     * @param xB               the argument X for a field B
     * @param xC               the argument X for a field C
     * @return the move of the player can be 1 for A, 2 for B
     *         and 3 for C fields
     */
    public abstract int move(int opponentLastMove, int xA, int xB, int xC);

    /**
     * String representation of an instance for easy distinguishing.
     * Unlike the general {@link Object#toString()} method,
     * it does not use the full package name, and uses constant length decimal hash.
     *
     * @return class name and unique id
     */
    @Override
    public String toString() {
        return String.format(
                "%s%010d",
                this.getClass().getSimpleName(),
                hashCode()
        );
    }
}
