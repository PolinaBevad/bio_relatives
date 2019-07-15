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

import genome.compare.common.ComparatorType;

/**
 * Defines a public class with configuration information parsed from
 * the command line input.
 *
 * @author Sergey Khvatov
 */
public class Configuration {

    /**
     * True, if help was requested, false otherwise.
     */
    public boolean isHelp = false;

    /**
     * Path to the common BED file.
     */
    public String pathToBed = "";

    /**
     * Path to the BAM file with the genome of the
     * first recipient (father).
     */
    public String pathToFirstRecipient = "";

    /**
     * Path to the BAM file with the genome of the
     * second recipient (mother).
     */
    public String pathToSecondRecipient = "";

    /**
     * Path to the BAM file with the genome of the
     * third recipient (son).
     */
    public String pathToThirdRecipient = "";

    /**
     * Number of people to be compared.
     */
    public int numberOfRecipients = 0;

    /**
     * Defines, whether intermediate information
     * should be printed or not.
     */
    public boolean intermediateOutput = false;

    /**
     * Defines number of threads that should be
     * created to process the information analysis.
     */
    public int threadsNumber = 1;

    /**
     * Defines the type of the comparator that will be used.
     */
    public ComparatorType type = ComparatorType.LEVENSHTEIN;

    /**
     * Path to the file with graph - default null
     */
    public String path = null;
}
