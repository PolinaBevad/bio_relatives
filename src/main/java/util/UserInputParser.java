package util;

import exception.CommandLineException;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.compare.comparator.AshkenaziTrioComparator;
import org.apache.commons.cli.*;

/**
 * This class implements a basic command line arguments parser
 * using org.apache.commons-cli.
 *
 * @author Sergey Khvatov
 * @author Vladislav Marchenko
 */
public class UserInputParser {

    /**
     * Tokens used as the keys for operations.
     */
    private static final String[] HELP_TOKENS = {"h", "help"};
    private static final String[] COMP2_TOKENS = {"c2", "compare2"};
    private static final String[] COMP3_TOKENS = {"c3", "compare3"};
    private static final String[] SET_OUT_TOKENS = {"io", "intermediateOutput"};

    /**
     * Enum with all the supported
     * operations.
     */
    public enum Operation {
        HELP, COMPARE_TWO_GENOMES, COMPARE_THREE_GENOMES, SET_INTERMEDIATE_OUTPUT;

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
                case SET_INTERMEDIATE_OUTPUT:
                    return new Option(SET_OUT_TOKENS[0], SET_OUT_TOKENS[1], false, "set an intermediate output flag");
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
        compareTwoOption.setArgs(3);
        compareTwoOption.setArgName("first> <second> <bed");
        options.addOption(compareTwoOption);

        // set compare three genomes option
        Option compareThreeOption = Operation.getOption(Operation.COMPARE_THREE_GENOMES);
        compareThreeOption.setArgs(4);
        compareThreeOption.setArgName("father> <mother> <son> <bed");
        options.addOption(compareThreeOption);

        // set setting intermediate output flag option
        Option setIntermediateOutputOption = Operation.getOption(Operation.SET_INTERMEDIATE_OUTPUT);
        setIntermediateOutputOption.setArgs(1);
        setIntermediateOutputOption.setArgName("<flag>");
        options.addOption(setIntermediateOutputOption);
    }

    /**
     * If this flag is true , then interim genome comparison results will be displayed,
     * else - only the main chromosome results will be obtained.
     */
    private static boolean intermediateOutput = false;

    public static String parseInput(String... input) throws Exception {
        try {
            // parse the input arguments
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, input);


            if (cmd.hasOption(SET_OUT_TOKENS[0])) {
                // get the arguments values
                String[] paths = cmd.getOptionValues(SET_OUT_TOKENS[0]);
                // check them
                if (paths == null || paths.length != 1 || (!paths[0].toLowerCase().equals("on") && !paths[0].toLowerCase().equals("off"))) {
                    throw new CommandLineException("incorrect keys and arguments were passed");
                }
                setIntermediateOutput(paths[0]);
            }
            if (cmd.hasOption(HELP_TOKENS[0])) {
                return showHelpMessage();
            } else if (cmd.hasOption(COMP2_TOKENS[0])) {
                // get the arguments values
                String[] paths = cmd.getOptionValues(COMP2_TOKENS[0]);
                // check them
                if (paths == null || paths.length != 3) {
                    throw new CommandLineException("incorrect keys and arguments were passed");
                }
                // if ok then call method
                return compareTwoGenomes(paths[0], paths[1], paths[2]);
            } else if (cmd.hasOption(COMP3_TOKENS[0])) {
                // get the arguments values
                String[] paths = cmd.getOptionValues(COMP3_TOKENS[0]);
                // check them
                if (paths == null || paths.length != 4) {
                    throw new CommandLineException("incorrect keys and arguments were passed");
                }
                // if ok then call method
                return compareThreeGenomes(paths[0], paths[1], paths[2], paths[3]);
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
     * @param args Two strings with the names of the bam files. and bed file.
     * @return Result of the comparison.
     * @throws GenomeException if some errors from comparing occurred
     * @throws GenomeFileException if files are invalid
     */
    private static String compareTwoGenomes(String... args) throws GenomeException, GenomeFileException {
        return AshkenaziTrioComparator.compareTwoGenomes(args[0], args[1], args[2], intermediateOutput);
    }

    /**
     * Compares three genomes (father, mother and son)
     * and return the result of the comparison.
     *
     * @param args Three strings with the names of the bam files and bed file.
     * @return Result of the comparison.
     * @throws GenomeException if some errors from comparing occurred
     * @throws GenomeFileException if files are invalid
     */
    private static String compareThreeGenomes(String... args) throws GenomeException, GenomeFileException{
        return AshkenaziTrioComparator.compareThreeGenomes(args[0], args[1], args[2], args[3], intermediateOutput);
    }

    /**
     * Set the intermediate output flag
     * @param arg string with the key word
     */
    private static void setIntermediateOutput(String arg) {
        if (arg.toLowerCase().equals("on")) {
            intermediateOutput = true;
        }
        else {
            intermediateOutput = false;
        }
    }
}
