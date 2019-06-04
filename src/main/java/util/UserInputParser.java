/**
 * MIT License
 *
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package util;

import exception.CommandLineException;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.compare.comparator.TrioComparator;
import org.apache.commons.cli.*;

import java.io.*;

/**
 * This class implements a basic command line arguments parser
 * using org.apache.commons-cli.
 *
 * @author Sergey Khvatov
 * @author Vladislav Marchenko
 */
public class UserInputParser {
    /**
     * Start string from file (@link README.md), which will print in help option
     */
    private static final String USAGE_STRING ="## Usage";
    /**
     * Finish string from file (@link README.md), which will print in help option
     */
    private static final String MAINTAINER_STRING ="## Maintainers";

    /**
     * Tokens used as the keys for operations.
     */
    private static final String[] HELP_TOKENS = {"h", "help"};
    private static final String[] COMP2_TOKENS = {"c2", "compare2"};
    private static final String[] COMP3_TOKENS = {"c3", "compare3"};
    private static final String[] TH_TOKENS = {"th", "threads"};
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
        setIntermediateOutputOption.setArgs(0);
        options.addOption(setIntermediateOutputOption);
    }

    /**
     * If this flag is true , then interim genome comparison results will be displayed,
     * else - only the main chromosome results will be obtained.
     */
    private static boolean intermediateOutput = false;

    public static String parseInput(String... input) throws CommandLineException {
        try {
            // parse the input arguments
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, input);

            if (cmd.hasOption(SET_OUT_TOKENS[0])) {
                setIntermediateOutput();
            }
            if (cmd.hasOption(HELP_TOKENS[0])) {
                return showHelpMessage();
            } else if (cmd.hasOption(COMP2_TOKENS[0])) {
                // getSAMRecordList the arguments values
                String[] paths = cmd.getOptionValues(COMP2_TOKENS[0]);
                // check them
                if (paths == null || paths.length != 3) {
                    throw new CommandLineException("incorrect keys and arguments were passed");
                }
                // if ok then call method
                return compareTwoGenomes(paths[0], paths[1], paths[2]);
            } else if (cmd.hasOption(COMP3_TOKENS[0])) {
                // getSAMRecordList the arguments values
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
        } catch (Exception ex) {
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
     * @throws IOException if file README.md does not exist
     */
    private static String showHelpMessage(String... args) throws IOException {
        StringBuilder help = new StringBuilder();
        File file = new File("README.md");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String line = reader.readLine();
        while (!line.equals(USAGE_STRING)) {
            line = reader.readLine();
        }
        help.append(line);
        help.append("\n");
        int tabNum = 0;
        int sharpCount = 2;
        while(!line.equals(MAINTAINER_STRING)) {
            line = reader.readLine();
            line  = line.replace("    j", "j");
            if (!line.equals(MAINTAINER_STRING)) {
                if (!line.contains("#")) {
                    help.append(getString((tabNum + 1), line));
                }
                else if(getSharpCount(line) == (sharpCount + 1)) {
                    help.append(getString(tabNum, line));
                }
                else if (getSharpCount(line) > (sharpCount + 1)) {
                    tabNum ++;
                    sharpCount = getSharpCount(line);
                    help.append(getString(tabNum, line));
                }
                else {
                    tabNum -= sharpCount -getSharpCount(line);
                    sharpCount = getSharpCount(line);
                    help.append(getString(tabNum, line));
                }
            }
        }
        return help.toString();
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
        String result = TrioComparator.compareTwoGenomes(args[0], args[1], args[2], intermediateOutput);
        intermediateOutput = false;
        return result;
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
        String result = TrioComparator.compareThreeGenomes(args[0], args[1], args[2], args[3], intermediateOutput);
        intermediateOutput = false;
        return result;
    }

    /**
     * Set the intermediate output flag as true
     */
    private static void setIntermediateOutput() {
        intermediateOutput = true;
    }

    /**
     * find count of # in input string
     * @param string input string in which we will define count of #
     * @return count of #
     */
    private static int getSharpCount(String string) {
        int k = 0;
        for (char c : string.toCharArray()) {
            if (c == '#') {
                k++;
            }
        }
        return k;
    }

    /**
     * Getting a string from file line with need count of tabs
     * @param tabNum count of tab
     * @param line line from file
     * @return need string
     */
    private static String getString(int tabNum, String line) {
        StringBuilder res = new StringBuilder();
        for (int i  = 0; i < tabNum; i++) {
            res.append("\t");
        }
        res.append(line);
        res.append("\n");
        return res.toString();
    }
}
