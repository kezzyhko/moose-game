package name.kezzyhlo.moose_game;


/**
 * This class contains convenient log function and different log level constants.
 */
@SuppressWarnings("WeakerAccess")
public final class Log {

    /**
     * No instances needed for this class
     */
    private Log() {}

    /**
     * Describes different log levels for {@link Log#log function}
     * @see Log#log
     * @see Log#LOG_LEVEL
     */
    enum LogLevel {

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
    static final LogLevel LOG_LEVEL = LogLevel.LOG_INFO;

    /**
     * If {@code level} is less than or equal to {@link Log#LOG_LEVEL},
     * this function will format and output string using {@code format} and {@code args}
     *
     * @param level Level of the message. Should be one of {@code Helper#LOG_*} constants.
     * @param format Format string
     * @param args Arguments to be substituted into the {@code format} string
     * @see Log#LOG_LEVEL
     */
    public static void log(LogLevel level, String format, Object... args) {
        if (level.ordinal() <= LOG_LEVEL.ordinal()) {
            System.out.printf(format, args);
        }
    }

}
