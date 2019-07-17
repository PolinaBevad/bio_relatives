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

import bam.regular.BAMParser;
import bam.regular.BEDFeature;
import bam.regular.BEDParser;
import exception.GenomeFileException;
import htsjdk.samtools.SAMRecord;
import org.junit.Before;
import org.junit.Test;
import genome.assembly.SAMRecordList;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link BAMParser} class methods.
 *
 * @author Vladislav Marchenko
 */
public class BAMParserTest {
    /**
     * Path to non existent file.
     */
    private static final String pathToNonExistentFile = "/wrong/path/file.bed";

    /**
     * Path to file with wrong extension.
     */
    private static final String pathToFileWithWrongExt = "src/test/resources/bam/BAMParser/file.txt";

    /**
     * Path to incorrect BAM file .
     */
    private static final String pathToIncorrectFile = "src/test/resources/bam/BAMParser/incorrect.bam";

    /**
     * Path to correct BAM file.
     */
    private static final String pathToCorrectFile = "src/test/resources/bam/BAMParser/cor.bam";

    /**
     * Path to directory.
     */
    private static final String pathToNotAFile = "src/test/resources/bam/BAMParser";

    /**
     * Path to the BED file.
     */
    private static final String pathToBEDFile = "src/test/resources/bam/BAMParser/correct.bed";

    /**
     * Array of SAMStrings for checking of correct work of method parse().
     */
    private static final String[] checkArray = {
            "SOLEXA-1GA-1_4_FC20ENL:7:39:938:359\t16\tchr1\t169797741\t25\t27M\t*\t0\t0\tGTGCAGTGGCATGACCATGGGTCACTG\tGERELNLHNKWLROR^VPNUYXT[hTc\tX0:i:1\tMD:Z:27\tNM:i:0\n",
            "SOLEXA-1GA-1_4_FC20ENL:7:266:936:393\t0\tchr1\t169821660\t25\t27M\t*\t0\t0\tACTTCTCATTGGGAGCCCTTTGGGACA\thhhhhhhhhhhhhhghhhhhhhYhhhh\tX0:i:1\tMD:Z:27\tNM:i:0\n",
            "SOLEXA-1GA-1_4_FC20ENL:7:1:838:683\t0\tchr1\t169844997\t25\t27M\t*\t0\t0\tAAAAAATTCAAGTCTCCAAATCTAAGA\tXLNQNLOKGERPJGHEAKDBNGNFFCG\tX0:i:1\tMD:Z:27\tNM:i:0\n",
            "SOLEXA-1GA-1_4_FC20ENL:7:178:334:611\t16\tchr1\t169852422\t25\t27M\t*\t0\t0\tGAGGGTAAGGCTCGCGTGCCCACTGCC\tEA@HBDFF?FG>HDEGDEJKKIYLSN_\tX1:i:1\tMD:Z:18G5G2\tNM:i:1\n"
    };

    /**
     * name of gene from bed file
     */
    private static final String geneName1 = "SCYL3";

    /**
     * name of gene from bed file
     */
    private static final String geneName2 = "C1orf112";

    /**
     * exons from bed file
     */
    private static Map<String, List<BEDFeature>> exons;

    @Before
    public void setUp() {
        exons = new BEDParser(pathToBEDFile).parse();
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNonExistentFile() {
        BAMParser parser = new BAMParser(pathToNonExistentFile);
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToFileWithWrongExtension() {
        BAMParser parser = new BAMParser(pathToFileWithWrongExt);
    }

    @Test
    public void CreationFromPathToCorrectFile() {
        BAMParser parser = new BAMParser(pathToCorrectFile);
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNotAFile() {
        BAMParser parser = new BAMParser(pathToNotAFile);
    }

    @Test(expected = GenomeFileException.class)
    public void ParsingIncorrectFile() {
        BAMParser parser = new BAMParser(pathToIncorrectFile);
        parser.parse(exons.get(geneName1));
    }

    @Test
    public void ParsingCorrectFile() {
        BAMParser parser = new BAMParser(pathToCorrectFile);
        SAMRecordList samRecords = parser.parse(exons.get(geneName2).get(0));
        int i = 0;
        for (SAMRecord samRecord: samRecords) {
            assertEquals(samRecord.getSAMString(), checkArray[i++]);
        }
    }
}
