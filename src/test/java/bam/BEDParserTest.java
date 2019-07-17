/*
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

package bam;

import bam.regular.BEDFeature;
import bam.regular.BEDParser;
import exception.GenomeFileException;
import org.junit.Test;

import java.util.*;

/**
 * Tests the {@link BEDParser} class methods.
 *
 * @author Sergey Hvatov
 */
public class BEDParserTest {
    /**
     * Path to non existent file.
     */
    private static final String pathToNonExistentFile = "/wrong/path/file.bed";

    /**
     * Path to file with wrong extension.
     */
    private static final String pathToFileWithWrongExt = "src/test/resources/bam/BEDParser/file.txt";

    /**
     * Path to incorrect bed file with wrong start and end position.
     */
    private static final String pathToIncorrectFile1 = "src/test/resources/bam/BEDParser/incorrect1.bed";

    /**
     * Path to incorrect bed file with wrong genome name.
     */
    private static final String pathToIncorrectFile2 = "src/test/resources/bam/BEDParser/incorrect2.bed";

    /**
     * Path to incorrect bed file with gene which contains in two chromosomes
     */
    private static final String pathToIncorrectFile3 ="src/test/resources/bam/BEDParser/incorrect3.bed";

    /**
     * Path to correct bed file.
     */
    private final String pathToCorrectFile = "src/test/resources/bam/BEDParser/correct.bed";

    /**
     * Path to directory.
     */
    private static final String pathToNotAFile = "src/test/resources/bam/BEDParser/";

    /**
     * Correct start position values from the file.
     */
    private static final int[] correctStart = {27612063, 24357004, 169853073, 169795048, 196651877, 23019447, 54784550, 24415793, 33013043};

    /**
     * Correct en position values from the file.
     */
    private static final int[] correctEnd = {27635277, 24413725, 169893959, 169854080,  196747504, 23083689,  54801198,  24469307, 33036992};

    /**
     * Correct name of the chromosome from the file.
     */
    private static final String correctName = "chr1";

    /**
     * Correct names of the genomes from the file.
     */
    private static final String[] correctGenome = {"FGR", "STPG1", "SCYL3", "C1orf112",  "CFH",  "KDM1A",  "TTC22", "NIPAL3", "AK2"};


    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNonExistentFile() {
        BEDParser parser = new BEDParser(pathToNonExistentFile);
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToFileWithWrongExtension() {
        BEDParser parser = new BEDParser(pathToFileWithWrongExt);
    }

    @Test
    public void CreationFromPathToCorrectFile() {
        BEDParser parser = new BEDParser(pathToCorrectFile);
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNotAFile() {
        BEDParser parser = new BEDParser(pathToNotAFile);
    }

    @Test(expected = GenomeFileException.class)
    public void ParsingIncorrectFileWithWrongStartEnd() {
        BEDParser parser = new BEDParser(pathToIncorrectFile1);
        Map<String, List<BEDFeature>> result = parser.parse();
    }

    @Test(expected = GenomeFileException.class)
    public void ParsingIncorrectFileWithWrongGenome() {
        BEDParser parser = new BEDParser(pathToIncorrectFile2);
        Map<String, List<BEDFeature>> result = parser.parse();
    }

    @Test(expected = GenomeFileException.class)
    public void ParsingIncorrectFileWithGeneContainsInTwoChromosomes() {
        BEDParser parser = new BEDParser(pathToIncorrectFile3);
        Map<String, List<BEDFeature>> result = parser.parse();
    }

    @Test
    public void ParsingCorrectFile() {
        BEDParser parser = new BEDParser(pathToCorrectFile);
        Map<String, List<BEDFeature>> result = parser.parse();
    }
}
