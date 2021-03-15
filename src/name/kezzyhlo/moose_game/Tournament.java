package name.kezzyhlo.moose_game;

import name.kezzyhlo.moose_game.players.*;

import java.util.*;


/**
 * Main class, which has the tournament code
 */
public final class Tournament {

    /**
     * List of all tactics, and amount of players for each tactic
     */
    private static final Map<Class<? extends Player>, Integer> PLAYERS = new HashMap<>();
    static {
        PLAYERS.put(RandomPlayer.class, 1);
        PLAYERS.put(RandomNonRepeatablePlayer.class, 1);
        PLAYERS.put(AlwaysSamePlayer.class, 1);
        PLAYERS.put(BestFieldPlayer.class, 10);
        PLAYERS.put(CopycatPlayer.class, 10);
        PLAYERS.put(MixedBestCopyPlayer.class, 3);
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
    public static double vegetationAmount(int x) {
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
    @SuppressWarnings("WeakerAccess")
    public static double payoff(int[] x, int move, int opponentMove) {
        if (move == opponentMove) return 0;
        if (move <= 0 || move > 3) return 0;
        return vegetationAmount(x[move]) - vegetationAmount(0);
    }

    /**
     * The main method.
     *
     * Performs tournament, where players defined in {@link Tournament#PLAYERS}
     * list are playing against each other in pairs.
     *
     * The scores are calculated, summed and outputted.
     * Different level of detail will be printed, depending on {@link Log#LOG_LEVEL}
     */
    public static void main(String[] args) {

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
                    if (move1 >= 1 && move1 <= 3) {
                        x[move1] -= 2;
                    }
                    if (move1 != move2 && move2 >= 1 && move2 <= 3) {
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
                    "%s: %18.10f (%.10f on average)\n",
                    String.format("%70s", player1),
                    score_sum,
                    score_sum / NUMBER_OF_ROUNDS / (players.size() - 1)
            );
            Log.log(Log.LogLevel.LOG_DETAILED_PAYOFFS, "\n");
            Log.log(Log.LogLevel.LOG_MOVES, "\n");
        }
    }

    /**
     * No instances needed for this class
     */
    private Tournament() {}


}
