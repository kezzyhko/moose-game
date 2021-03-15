package name.kezzyhlo.moose_game.players;


import name.kezzyhlo.moose_game.Random;
import name.kezzyhlo.moose_game.Tournament;

/**
 * This strategy tries to cooperate with other players that uses the same strategy.
 * In starts by randomly choosing the moves, until it chooses different move from the opponent.
 * When that happens, it remembers those randomly chosen fields, one as "mine", and one as "opponent's".
 * Then, it waits on the third field, so that remembered two fields can grow.
 * After they grow, it starts switching between the waiting spot and "my" spot.
 * If opponent tries to eat "my" field, then it switches to {@link MixedBestCopyPlayer} strategy
 */
public class CoopPlayer extends Player {

    // Constants / settings

    /**
     * Different stages of the strategy
     * @see CoopPlayer#state
     */
    private enum State {

        /**
         * At this state, just random moves will be selected to agree on "our" fields
         */
        STATE_START,

        /**
         * At this state, we will wait for fields to grow
         * @see CoopPlayer#TIMES_TO_WAIT
         */
        STATE_WAIT,

        /**
         * At this state, we will switch between moving to "our" field and waiting to grow
         */
        STATE_EAT,

        /**
         * This state means that the opponent is not playing the same strategy
         * @see CoopPlayer#ANOTHER_STRATEGY
         */
        STATE_NOT_COOP,
    }

    /**
     * This constant denotes how many times should we wait while in {@link State#STATE_WAIT}.
     * This particular number was chosen, because {@link Tournament#vegetationAmount(int)}
     * grows insignificantly for larger {@code X} values
     */
    private static final int TIMES_TO_WAIT = 5;

    /**
     * This is the fallback strategy that will be used in {@link State#STATE_NOT_COOP}
     */
    private final Player ANOTHER_STRATEGY = new MixedBestCopyPlayer();


    // Variables / internal state

    /**
     * Current state.
     * @see State
     */
    private State state = State.STATE_START;

    /**
     * The move, made by this player in previous round
     */
    private int myLastMove = 0;

    /**
     * Which move should this strategy do during {@link State#STATE_WAIT}
     * @see State#STATE_WAIT
     */
    private int waitMove = 0;

    /**
     * How many times we already waited in {@link State#STATE_WAIT} state
     * @see State#STATE_WAIT
     * @see CoopPlayer#TIMES_TO_WAIT
     */
    private int timesWaited = 0;

    /**
     * Which move this strategy should do during {@link State#STATE_EAT}
     * @see State#STATE_EAT
     */
    private int eatMove = 0;


    // Functions

    /**
     * Resets all remembered moves and counters to {@code 0}, and sets
     * {@link CoopPlayer#state} variable to {@link State#STATE_START}
     */
    @Override
    public void reset() {
        state = State.STATE_START;
        waitMove = 0;
        eatMove = 0;
        timesWaited = 0;
        myLastMove = 0;
    }

    /**
     * Performs the move according to the strategy and depending on the {@link CoopPlayer#state}.
     * Each state can either return a move (and remember it) or change the state and continue the execution.
     */
    @Override
    public int move(int opponentLastMove, int xA, int xB, int xC) {

        if (state == State.STATE_START) {
            // at the very beginning
            if (opponentLastMove == myLastMove) {
                // random moves until moves do not match
                myLastMove = Random.randomMove();
                return myLastMove;
            } else {
                // when moves are different, change state and remember some fields
                state = State.STATE_WAIT;
                eatMove = myLastMove;
                waitMove = 1 + 2 + 3 - myLastMove - opponentLastMove;
            }
        } else if (opponentLastMove == eatMove) {
            // if we already decided on the spots, and opponent is trying to eat my field
            state = State.STATE_NOT_COOP;
        }

        if (state == State.STATE_WAIT) {
            if (timesWaited < TIMES_TO_WAIT) {
                // wait for fields to grow
                timesWaited++;
                myLastMove = waitMove;
                return waitMove;
            } else {
                // waited enough, let's eat
                state = State.STATE_EAT;
            }
        }

        if (state == State.STATE_EAT) {
            if (myLastMove == waitMove) {
                // if we waited last round, then we should eat
                myLastMove = eatMove;
                return eatMove;
            } else {
                // if we ate the last round, then we should wait
                myLastMove = waitMove;
                return waitMove;
            }
        }

        // state = State.STATE_NOT_FRIEND
        return ANOTHER_STRATEGY.move(opponentLastMove, xA, xB, xC);
    }

}
