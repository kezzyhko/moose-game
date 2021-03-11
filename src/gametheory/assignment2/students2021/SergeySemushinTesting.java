/*
 * SergeySemushinTesting
 *
 * Made by Sergey Semushin
 * Group: BS18-SB-01 group
 *
 * This file contains code for testing different strategies in a tournament.
 */

package gametheory.assignment2.students2021;

import gametheory.assignment2.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;



public final class SergeySemushinTesting {


    // Game rules and simulation

    /**
     * List of all tactics, and amount of players for each tactic
     */
    private static final Map<Class<? extends Player>, Integer> PLAYERS = new HashMap<>();
    static {
        PLAYERS.put(RandomPlayer.class, 1);
        PLAYERS.put(RandomNonRepeatablePlayer.class, 2);
        PLAYERS.put(BestFieldPlayer.class, 3);
        PLAYERS.put(AlwaysSamePlayer.class, 1);
        PLAYERS.put(CopycatPlayer.class, 3);
        PLAYERS.put(MixedBestCopyPlayer.class, 2);
        PLAYERS.put(CoopPlayer.class, 2);
    }

    /**
     * Defines amount of rounds in each match of the tournament
     */
    private static final int NUMBER_OF_ROUNDS = Random.randomInt(100, 1000);

    /**
     * Calculates the amount of vegetation on a field, given parameter X of this field
     *
     * @param x parameter X of the field
     * @return the amount of vegetation on a field
     */
    private static double vegetationAmount(int x) {
        return 10 * Math.exp(x) / (1 + Math.exp(x));
    }

    /**
     * Calculates the payoff of one player's move
     *
     * @param x Current values of X for each of fields
     * @param move The move made by the player
     * @param opponentMove The move made by the opponent
     * @return The payoff that should be received by the player on the current move
     */
    private static double payoff(int[] x, int move, int opponentMove) {
        if (move == opponentMove) return 0;
        return vegetationAmount(x[move]) - vegetationAmount(0);
    }

    /**
     * The main method.
     *
     * Performs tournament, where players defined in {@link SergeySemushinTesting#PLAYERS}
     * list are playing against each other in pairs.
     *
     * The scores are calculated, summed and outputted.
     * Different level of detail will be printed, depending on {@link Log#LOG_LEVEL}
     */
    @SuppressWarnings("WeakerAccess")
    public static void tournament() {

        // create list of player instances
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Class<? extends Player>, Integer> entry : PLAYERS.entrySet()) {
            try {
                int amount = entry.getValue();
                Class<? extends Player> playerClass = entry.getKey();
                for (int i = 0; i < amount; i++) {
                    players.add(playerClass.newInstance());
                }
            } catch (ReflectiveOperationException e) {
                Log.log(Log.LogLevel.LOG_ONLY_ERRORS, "Something went wrong during creating %s\n", entry.getKey().getSimpleName());
                e.printStackTrace();
            }
        }

        Log.log(Log.LogLevel.LOG_INFO, "Number of rounds: %d\n", NUMBER_OF_ROUNDS);
        Log.log(Log.LogLevel.LOG_INFO, "Number of players: %d\n", players.size());
        Log.log(Log.LogLevel.LOG_INFO, "\n");

        for (Player player1 : players) {
            double score_sum = 0;
            for (Player player2 : players) {
                if (player1 == player2) continue;

                Log.log(
                        Log.LogLevel.LOG_MOVES,
                        "%s is playing with %s\n",
                        player1, player2
                );

                // initialize players and the game
                player1.reset();
                player2.reset();
                double score1 = 0, score2 = 0;
                int[] x = {-1, 1, 1, 1};
                int prevMove1 = 0, prevMove2 = 0;

                for (int i = 0; i < NUMBER_OF_ROUNDS; i++) {

                    // get players' moves
                    int move1 = player1.move(prevMove2, x[1], x[2], x[3]);
                    int move2 = player2.move(prevMove1, x[1], x[2], x[3]);

                    // update players' scores
                    score1 += payoff(x, move1, move2);
                    score2 += payoff(x, move2, move1);

                    // log detailed info
                    Log.log(
                            Log.LogLevel.LOG_MOVES,
                            "Round %d. Vegetation: %s. %s chooses %d and %s chooses %d. Scores: %f, %f.\n",
                            i,
                            Arrays.toString(x),
                            player1, move1, player2, move2,
                            score1, score2
                    );

                    // update the X values of all fields
                    x[move1] -= 2;
                    if (move1 != move2) {
                        x[move2] -= 2;
                    }
                    for (int j = 1; j <= x.length - 1; j++) {
                        x[j]++;
                        if (x[j] < 0) {
                            x[j] = 0;
                        }
                    }

                    // save moves for later
                    prevMove1 = move1;
                    prevMove2 = move2;

                }

                Log.log(Log.LogLevel.LOG_DETAILED_PAYOFFS,
                        "Payoffs: %f (%f on average) for %s and %f (%f on average) for %s\n",
                        score1, score1 / NUMBER_OF_ROUNDS, player1,
                        score2, score2 / NUMBER_OF_ROUNDS, player2
                );
                Log.log(Log.LogLevel.LOG_MOVES, "\n");

                score_sum += score1;
            }

            Log.log(
                    Log.LogLevel.LOG_PAYOFFS_SUM,
                    "%s: %18.10f (%18.10f on average)\n",
                    String.format("%50s", player1),
                    score_sum,
                    score_sum / NUMBER_OF_ROUNDS / (players.size() - 1)
            );
            Log.log(Log.LogLevel.LOG_DETAILED_PAYOFFS, "\n");
            Log.log(Log.LogLevel.LOG_MOVES, "\n");
        }

    }



    // System methods

    /**
     * Main function with the appropriate signature.
     * Ignores {@code args}, calls {@link SergeySemushinTesting#tournament()}
     *
     * @param args This parameter is ignored.
     */
    public static void main(String[] args) {
        tournament();
    }

    /**
     * No instances needed for this class
     */
    private SergeySemushinTesting() {}



    // Helper classes

    /**
     * This class contains convenient log function and different log level constants.
     */
    private static final class Log {

        /**
         * No instances needed for this class
         */
        private Log() {}

        /**
         * Describes different log levels for {@link Log#log function}
         * @see Log#log
         * @see Log#LOG_LEVEL
         */
        private enum LogLevel {

            /**
             * Do not print any info, only log errors
             */
            LOG_ONLY_ERRORS,

            /**
             * Additionally to previous {@link LogLevel} constants,
             * print calculated total score for each player
             */
            LOG_PAYOFFS_SUM,

            /**
             * Additionally to previous {@link LogLevel} constants,
             * logs additional info, like number of rounds or total amount of players
             */
            LOG_INFO,

            /**
             * Additionally to previous {@link LogLevel} constants,
             * logs payoffs of each match
             */
            LOG_DETAILED_PAYOFFS,

            /**
             * Additionally to previous {@link LogLevel} constants,
             * prints detailed logs with information about each round
             */
            LOG_MOVES,

        }

        /**
         * Current log level
         * @see LogLevel
         * @see Log#log
         */
        private static final LogLevel LOG_LEVEL = LogLevel.LOG_INFO;

        /**
         * If {@code level} is less than or equal to {@link Log#LOG_LEVEL},
         * this function will format and output string using {@code format} and {@code args}
         *
         * @param level Level of the message. Should be one of {@code Helper#LOG_*} constants.
         * @param format Format string
         * @param args Arguments to be substituted into the {@code format} string
         * @see Log#LOG_LEVEL
         */
        @SuppressWarnings("WeakerAccess")
        public static void log(LogLevel level, String format, Object... args) {
            if (level.ordinal() <= LOG_LEVEL.ordinal()) {
                System.out.printf(format, args);
            }
        }

    }

    /**
     * This class contains functions for getting random integers, random from a list of choices, etc.
     */
    @SuppressWarnings("WeakerAccess")
    private static final class Random {

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



    //Definition of different player classes, with different strategies

    /**
     * Abstract class with basic things defined
     */
    abstract protected static class AbstractPlayer implements Player {

        private static final String THE_EMAIL = "s.semushin@innopolis.university";

        @Override
        public String getEmail() {
            return THE_EMAIL;
        }

        @Override
        public void reset() {}

        /**
         * String representation of an instance for easy distinguishing.
         * Unlike the general {@link Object#toString()} method,
         * it does not use the full package name, and uses constant length decimal hash.
         *
         * {@inheritDoc}
         *
         * @return short the name of the class and hash
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

    /**
     * This strategy just chooses random field.
     */
    protected static class RandomPlayer extends AbstractPlayer {

        @Override
        public int move(int opponentLastMove, int xA, int xB, int xC) {
            return Random.randomMove();
        }

    }

    /**
     * This strategy chooses random field like {@link RandomPlayer},
     * but it will not choose the same field twice in a row
     */
    protected static class RandomNonRepeatablePlayer extends AbstractPlayer {

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

    /**
     * This is greedy strategy, which chooses the field with the best X value at the current round.
     * If there are two or more fields with the best X value, then it will select randomly between them.
     */
    protected static class BestFieldPlayer extends AbstractPlayer {

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

    /**
     * This strategy selects random move in the first round, and then continues selecting it each time.
     */
    protected static class AlwaysSamePlayer extends AbstractPlayer {

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

    /**
     * This strategy selects random move in the first round, and then it chooses the same move,
     * which the opponents selected in the previous round.
     * If such a move will lead to the payoff of 0, then it selects the random move from others.
     */
    protected static class CopycatPlayer extends AbstractPlayer {

        @Override
        public int move(int opponentLastMove, int xA, int xB, int xC) {
            int[] x = {-1, xA, xB, xC};
            if (opponentLastMove == 0) {
                return Random.randomMove();
            } else if (x[opponentLastMove] != 0) {
                return opponentLastMove;
            } else {
                return Random.randomMoveExcluding(opponentLastMove);
            }
        }

    }

    /**
     * This strategy selects randomly each round between {@link CopycatPlayer} and {@link BestFieldPlayer} tactics.
     */
    protected static class MixedBestCopyPlayer extends AbstractPlayer {

        List<Player> tactics = Arrays.asList(
                new CopycatPlayer(),
                new BestFieldPlayer()
        );

        @Override
        public int move(int opponentLastMove, int xA, int xB, int xC) {
            return Random.randomFromList(tactics).move(opponentLastMove, xA, xB, xC);
        }

    }

    /**
     * This strategy tries to coop with other players that uses the same strategy.
     * In starts by randomly choosing the moves, until it chooses different move from the opponent.
     * When that happens, it remembers those randomly chosen fields.
     * Then, it waits on the third field, so that remembered two fields can grow.
     * After they grow, it starts switching between the waiting spot and its remembered spot.
     * If something goes not according to the plan (the opponent does an unexpected thing),
     * then it switches to {@link MixedBestCopyPlayer} strategy
     */
    protected static class CoopPlayer extends AbstractPlayer {

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
         * This particular number was chosen, because {@link SergeySemushinTesting#vegetationAmount(int)}
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

        /**
         * Which move should the opponent do during {@link State#STATE_EAT},
         * if he is playing by the same strategy
         * @see State#STATE_EAT
         */
        private int opponentEatMove = 0;


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
            opponentEatMove = 0;
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
                if (opponentLastMove == myLastMove) {
                    // random moves until moves do not match
                    myLastMove = Random.randomMove();
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

            // state = State.STATE_NOT_FRIEND
            return ANOTHER_STRATEGY.move(opponentLastMove, xA, xB, xC);
        }

    }


}
