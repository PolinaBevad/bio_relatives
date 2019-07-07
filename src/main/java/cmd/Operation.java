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

import exception.GenomeException;
import exception.GenomeFileException;
import util.TrioComparator;

/**
 * This class implements a basic command line arguments parser
 * using org.apache.commons-cli.
 *
 * @author Sergey Khvatov
 */
public class Operation {

    /**
     * Help message that will be shown if help is requested.
     */
    private static final String HELP_MESSAGE = "## Usage\n" +
        "\tjava -jar bio_relatives.jar [-h | --help] [-io | --intermediateOutput][-c2 | --compare2 <first> <second> <bed>] [-c3 | --compare3 <father> <mother> <son> <bed>] \n" +
        "### Options\n" +
        "\t\n" +
        "\t`-h`, `--help` - show help message.\n" +
        "\t\n" +
        "\t`-io`, `--intermediateOutput` - key, which enables intermediate results output.\n" +
        "\t\n" +
        "\t`-c2`, `--compare2` - compare genomes of two persons.\n" +
        "\t\n" +
        "\t`-c3`, `--compare3` - compare genomes of three persons (father/mother/son).\n" +
        "\t\n" +
        "### Examples\n" +
        "\t#### Comparison of the genomes of two persons\n" +
        "\t\t```\n" +
        "\t\tjava -jar bio_relatives.jar --compare2 ~/path/to/first.bam ~/path/to/second.bam ~/path/to/file.bed\n" +
        "\t\t```\n" +
        "\t#### Comparison of the genomes of three persons\n" +
        "\t\t```\n" +
        "\t\tjava -jar bio_relatives.jar --compare3 ~/path/to/father.bam ~/path/to/mother.bam ~/path/to/son.bam ~/path/to/file.bed\n" +
        "\t\t```\n" +
        "\t#### Comparison of the genomes with intermediate output\n" +
        "\t\t```\n" +
        "\t\tjava -jar bio_relatives.jar -io -c2 ~/path/to/first.bam ~/path/to/second.bam ~/path/to/file.bed\n" +
        "\t\t```\n";

    /**
     * Start the execution of the program according to
     * the configuration, parsed from the command line.
     *
     * @param config Configuration object.
     * @return Result of the execution of the program with this arguments.
     * @throws GenomeException
     * @throws GenomeFileException if error occurs while comparing genomes.
     */
    public String start(Configuration config) throws GenomeFileException, GenomeException{
        if (config.numberOfRecipients == 2) {
            return TrioComparator.compareTwoGenomes(config.pathToFirstRecipient, config.pathToSecondRecipient, config.pathToBed, config.type, config.threadsNumber, config.intermediateOutput);
        } else if (config.numberOfRecipients == 3) {
            return TrioComparator.compareThreeGenomes(config.pathToFirstRecipient, config.pathToSecondRecipient, config.pathToThirdRecipient, config.pathToBed, config.type, config.threadsNumber, config.intermediateOutput);
        } else {
            return HELP_MESSAGE;
        }
    }
}
