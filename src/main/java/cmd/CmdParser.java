/**
 * MIT License
 * <p>
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cmd;

import exception.CommandLineException;
import genome.compare.ComparatorType;
import org.apache.commons.cli.*;

/**
 * User input through command line parser.
 *
 * @author Sergey Khvatov
 */
public class CmdParser {

    /**
     * Parses the arguments from the command line.
     *
     * @param args User input arguments.
     * @return User input arguments.
     * @throws CommandLineException if error occurs while parsing user input.
     */
    public Configuration parseCommandLine(String... args) {
        try {
            Options options = this.buildOptions();
            CommandLineParser cmd = new DefaultParser();
            return parseParameters(cmd.parse(options, args));
        } catch (ParseException pex) {
            CommandLineException cmdex = new CommandLineException(pex.getMessage());
            cmdex.initCause(pex);
            throw cmdex;
        }
    }

    /**
     * Parses the parameters and builds the configuration object.
     *
     * @param cmd User input arguments.
     * @return User input arguments.
     * @throws CommandLineException if error occurs while parsing user input.
     */
    private Configuration parseParameters(CommandLine cmd) {
        Configuration config = new Configuration();

        // check main arguments
        if (cmd.hasOption("h")) {
            config.isHelp = true;
            return config;
        } else if (cmd.hasOption("c2")) {
            config.numberOfRecipients = 2;
            String[] paths = cmd.getOptionValues("c2");
            if (paths == null || paths.length != 3) {
                throw new CommandLineException("Incorrect keys and arguments were passed! See help for more info.");
            }
            config.pathToFirstRecipient = paths[0];
            config.pathToSecondRecipient = paths[1];
            config.pathToBed = paths[2];
        } else if (cmd.hasOption("c3")) {
            config.numberOfRecipients = 3;
            String[] paths = cmd.getOptionValues("c3");
            if (paths == null || paths.length != 4) {
                throw new CommandLineException("Incorrect keys and arguments were passed! See help for more info.");
            }
            config.pathToFirstRecipient = paths[0];
            config.pathToSecondRecipient = paths[1];
            config.pathToThirdRecipient = paths[2];
            config.pathToBed = paths[3];
        } else {
            throw new CommandLineException("Incorrect user input! Type [-h] or [--help] for help!");
        }

        // check if advanced output is requested
        if (cmd.hasOption("io")) {
            config.intermediateOutput = true;
        }

        // check if advanced output is requested
        if (cmd.hasOption("m")) {
            String mode = cmd.getOptionValue("m").toUpperCase();
            switch (mode) {
                case "X":
                    config.type = ComparatorType.X_STR;
                    break;
                case "Y":
                    config.type = ComparatorType.Y_STR;
                    break;
                case "L":
                    config.type = ComparatorType.LEVENSHTEIN;
                    break;
                default:
                    throw new CommandLineException("Incorrect user input! Type [-h] or [--help] for help!");
            }
        }

        // check if number of threads was changed
        if (cmd.hasOption("th")) {
            int threadsNum = Integer.parseInt(cmd.getOptionValue("th"));
            if (threadsNum < 1) {
                throw new CommandLineException("Incorrect keys and arguments were passed! See help for more info.");
            }
            config.threadsNumber = threadsNum;
        }

        return config;
    }

    /**
     * Builds the {@link org.apache.commons.cli.Options} class object
     * with all the options, that may appear in the input.
     *
     * @return Options class object.
     */
    private Options buildOptions() {
        // temporary object
        Options options = new Options();

        options.addOption(
            Option.builder("h")
            .longOpt("help")
            .desc("Prints out help message with the information about how program works.")
            .hasArg(false)
            .build()
        );

        options.addOption(
            Option.builder("c2")
            .longOpt("compare2")
            .desc("Compares two genomes.")
            .hasArg()
            .numberOfArgs(3)
            .argName("first> <second> <bed")
            .type(String.class)
            .build()
        );

        options.addOption(
            Option.builder("c3")
                .longOpt("compare3")
                .desc("Compares three genomes.")
                .hasArg()
                .numberOfArgs(4)
                .argName("father> <mother> <son> <bed")
                .type(String.class)
                .build()
        );

        options.addOption(
            Option.builder("m")
                .longOpt("mode")
                .desc("Defines, which comparator will be used.")
                .hasArg()
                .numberOfArgs(1)
                .argName("mode: X, Y, L")
                .type(String.class)
                .build()
        );

        options.addOption(
            Option.builder("io")
                .longOpt("intermediateOutput")
                .desc("Defines, whether intermediate information should be printed or not.")
                .hasArg(false)
                .build()
        );

        options.addOption(
            Option.builder("th")
                .longOpt("threadsNumber")
                .desc("Defines number of threads that should be created to process the information analysis.")
                .hasArg()
                .type(Integer.class)
                .build()
        );

        return options;
    }
}
