package util;

import exception.CommandLineException;
import org.apache.commons.cli.*;

/**
 * This class implements a basic command line arguments parser
 * using org.apache.commons-cli.
 *
 * @author Sergey Khvatov
 */
public class UserInputParser {

    /**
     * Tokens used as the keys for operations.
     */
    private static final String[] HELP_TOKENS = {"h", "help"};
    private static final String[] COMP2_TOKENS = {"c2", "compare2"};
    private static final String[] COMP3_TOKENS = {"c3", "compare3"};

    /**
     * Enum with all the supported
     * operations.
     */
    public enum Operation {
        HELP, COMPARE_TWO_GENOMES, COMPARE_THREE_GENOMES;

        /**
         * Get the option representation of each operation.
         *
         * @param op Required operation.
         * @return Pair of strings with short and long representations
         * of each operation.
         */
        public static Option getOption(Operation op) {
            switch (op) {
                case HELP:
                    return new Option(HELP_TOKENS[0], HELP_TOKENS[1], false, "show help message");
                case COMPARE_TWO_GENOMES:
                    return new Option(COMP2_TOKENS[0], COMP2_TOKENS[1], false, "compare two genomes operation");
                case COMPARE_THREE_GENOMES:
                    return new Option(COMP3_TOKENS[0], COMP3_TOKENS[1], false, "compare three genomes operation");
                default:
                    return new Option("null", "null");
            }
        }
    }

    /**
     * Array of options.
     */
    private static Options options = new Options();

    // initialization of available operations and keys
    static {
        // set help option
        options.addOption(Operation.getOption(Operation.HELP));

        // set compare two genomes option
        Option compareTwoOption = Operation.getOption(Operation.COMPARE_TWO_GENOMES);
        compareTwoOption.setArgs(2);
        compareTwoOption.setArgName("first> <second");
        options.addOption(compareTwoOption);

        // set compare two genomes option
        Option compareThreeOption = Operation.getOption(Operation.COMPARE_THREE_GENOMES);
        compareThreeOption.setArgs(3);
        compareThreeOption.setArgName("father> <mother> <son");
        options.addOption(compareThreeOption);
    }

    public static String parseInput(String... input) throws CommandLineException {
        try {
            // parse the input arguments
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, input);

            if (cmd.hasOption(HELP_TOKENS[0])) {
                return showHelpMessage();
            } else if (cmd.hasOption(COMP2_TOKENS[0])) {
                // get the arguments values
                String[] paths = cmd.getOptionValues(COMP2_TOKENS[0]);
                // check them
                if (paths == null || paths.length != 2) {
                    throw new CommandLineException("incorrect keys and arguments were passed");
                }
                // if ok then call method
                return compareTwoGenomes(paths[0], paths[1]);
            } else if (cmd.hasOption(COMP3_TOKENS[0])) {
                // get the arguments values
                String[] paths = cmd.getOptionValues(COMP3_TOKENS[0]);
                // check them
                if (paths == null || paths.length != 3) {
                    throw new CommandLineException("incorrect keys and arguments were passed");
                }
                // if ok then call method
                return compareThreeGenomes(paths[0], paths[1], paths[2]);
            } else {
                return showHelpMessage();
            }
        } catch (ParseException ex) {
            CommandLineException cmdex = new CommandLineException("exception occurred [" + ex.getClass().getName() + "]: [" + ex.getMessage() + "]");
            cmdex.initCause(ex);
            throw cmdex;
        }
    }

    /**
     * Shows the help message if it was requested.
     *
     * @param args Not used variable.
     * @return Help message.
     */
    private static String showHelpMessage(String... args) {
        return "help";
    }

    /**
     * Compares two genomes and return the result of the comparison.
     *
     * @param args Two strings with the names of the bam files.
     * @return Result of the comparison.
     */
    private static String compareTwoGenomes(String... args) {
        return "cmp2";
    }

    /**
     * Compares three genomes (father, mother and son)
     * and return the result of the comparison.
     *
     * @param args Three strings with the names of the bam files.
     * @return Result of the comparison.
     */
    private static String compareThreeGenomes(String... args) {
        return "cmp3";
    }
}
