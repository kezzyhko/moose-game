/*
 * SergeySemushinCode
 *
 * Made by Sergey Semushin
 * Group: BS18-SB-01 group
 *
 * This file contains the collected code with necessary and sufficient parts for the final submission.
 */

package gametheory.assignment2.students2021;

import gametheory.assignment2.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;



/**
 * This strategy tries to coop with other players that uses the same strategy.
 * In starts by randomly choosing the moves, until it chooses different move from the opponent.
 * When that happens, it remembers those randomly chosen fields.
 * Then, it waits on the third field, so that remembered two fields can grow.
 * After they grow, it starts switching between the waiting spot and its remembered spot.
 *
 * If something goes not according to the plan (the opponent does an unexpected thing),
 * then it switches to another strategy: each time, it randomly selects between the field
 * with the best X value and the field, which opponent has chosen previously.
 * If there are two or more fields with the best X value, then it will select randomly between them.
 * If previous opponent's move will lead to the payoff of 0, then it selects the random move from others.
 */
public class SergeySemushinCode implements Player {


    // Constants / settings

    /**
     * The email for {@link SergeySemushinCode#getEmail()}
     */
    private static final String THE_EMAIL = "s.semushin@innopolis.university";

    /**
     * Different stages of the strategy
     * @see SergeySemushinCode#state
     */
    private enum State {

        /**
         * At this state, just random moves will be selected to agree on "our" fields
         */
        STATE_START,

        /**
         * At this state, we will wait for fields to grow
         * @see SergeySemushinCode#TIMES_TO_WAIT
         */
        STATE_WAIT,

        /**
         * At this state, we will switch between moving to "our" field and waiting to grow
         */
        STATE_EAT,

        /**
         * This state means that the opponent is not playing the same strategy
         */
        STATE_NOT_COOP,
    }

    /**
     * This constant denotes how many times should we wait while in {@link State#STATE_WAIT}.
     * This particular number was chosen, because vegetation will grow insignificantly for larger {@code X} values
     */
    private static final int TIMES_TO_WAIT = 5;



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
     * @see SergeySemushinCode#TIMES_TO_WAIT
     */
    private int timesWaited = 0;

    /**
     * Which move this strategy should do during {@link State#STATE_EAT}
     * @see State#STATE_EAT
     */
    private int eatMove = 0;

    /**
     * Which move should the opponent do during {@link State#STATE_EAT},
     * if he is playing by the same strategy
     * @see State#STATE_EAT
     */
    private int opponentEatMove = 0;



    // Helper functions

    /**
     * Returns random integer between {@code min} and {@code max}
     * @param min Lower bound (including {@code min} itself)
     * @param max Upper bound (including {@code max} itself)
     * @return Random integer between {@code min} and {@code max}
     */
    private static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }

    /**
     * Returns random move
     *
     * @return Random move
     */
    private static int randomMove() {
        return randomInt(1, 3);
    }

    /**
     * Returns random move, excluding {@code m}
     *
     * @param m Move to exclude
     * @return Random move, excluding {@code m}
     */
    private static int randomMoveExcluding(int m) {
        return (randomInt(1, 2) + m - 1) % 3 + 1;
    }

    /**
     * Returns random value from the array {@code list}
     *
     * @param list Array of values to select from
     * @return Random value from the {@code list} array
     */
    private static <T> T randomFromList(List<T> list) {
        return list.get(randomInt(0, list.size() - 1));
    }



    // Public functions

    public String getEmail() {
        return THE_EMAIL;
    }

    /**
     * Resets all remembered moves and counters to {@code 0}, and sets
     * {@link SergeySemushinCode#state} variable to {@link State#STATE_START}
     */
    @Override
    public void reset() {
        state = State.STATE_START;
        waitMove = 0;
        eatMove = 0;
        opponentEatMove = 0;
        timesWaited = 0;
        myLastMove = 0;
    }

    /**
     * Performs the move according to the strategy and depending on the {@link SergeySemushinCode#state}.
     * Each state can either return a move (and remember it) or change the state and continue the execution.
     */
    public int move(int opponentLastMove, int xA, int xB, int xC) {

        if (state == State.STATE_START) {
            if (opponentLastMove == myLastMove) {
                // random moves until moves do not match
                myLastMove = randomMove();
                return myLastMove;
            } else {
                // when moves are different, change state and remember some fields
                state = State.STATE_WAIT;
                eatMove = myLastMove;
                opponentEatMove =  opponentLastMove;
                waitMove = 1 + 2 + 3 - myLastMove - opponentLastMove;
            }
        }

        if (state == State.STATE_WAIT) {
            if (timesWaited != 0 && opponentLastMove != waitMove) {
                // if the opponent does not wait on the agreed spot
                state = State.STATE_NOT_COOP;
            } else if (timesWaited < TIMES_TO_WAIT) {
                // if all ok, we wait
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
                // if we waited last round, ...
                if (opponentLastMove == waitMove) {
                    // and the opponent did the same, then we should eat
                    myLastMove = eatMove;
                    return eatMove;
                } else {
                    // but the opponent didn't, the he deviated from the strategy
                    state = State.STATE_NOT_COOP;
                }
            } else {
                // if we ate the last round, ...
                if (opponentLastMove == opponentEatMove) {
                    // and the opponent did the same, then we should wait
                    myLastMove = waitMove;
                    return waitMove;
                } else {
                    // but the opponent didn't, the he deviated from the strategy
                    state = State.STATE_NOT_COOP;
                }
            }
        }

        // state = State.STATE_NOT_COOP
        int rand = randomInt(1, 2);
        int[] x = {-1, xA, xB, xC};
        if (rand == 1) {
            // randomly select one of the fields with the best X value
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
            return randomFromList(maxIndexes);
        } else {
            // select previous opponent's move, or, if it will lead to 0, select random move from others
            if (x[opponentLastMove] != 0) {
                return opponentLastMove;
            } else {
                return randomMoveExcluding(opponentLastMove);
            }
        }
    }


}
