package bam;

import exception.GenomeException;
import exception.GenomeFileException;
import htsjdk.samtools.SAMRecord;
import org.junit.Test;

import java.util.ArrayList;

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
        "SOLEXA-1GA-1_4_FC20ENL:7:228:514:40\t16\tchr1\t169864402\t25\t27M\t*\t0\t0\tAAAAACATAAAAATAACTGGAGAGGCC\tX\\Z]JMYQh_XfhMWKfPUOZVVJNhh\tX0:i:1\tMD:Z:27\tNM:i:0\n",
        "SOLEXA-1GA-1_1_FC20EMA:7:229:472:221\t16\tchr1\t169869255\t25\t27M\t*\t0\t0\tTTTGCTCTAGCGCTTCCTTTTCCTGTC\tLKJEPGTJWLXMSQOW\\MNSKShZcdh\tX0:i:1\tMD:Z:27\tNM:i:0\n",
        "SOLEXA-1GA-1_4_FC20ENL:7:241:446:659\t16\tchr1\t169884971\t25\t27M\t*\t0\t0\tAACTGAAGGTGCAGTTCTCTAGGGAGG\tBBJAWFCOUFCDB@FDWAJDWQPL[gY\tX1:i:1\tMD:Z:13G2G10\tNM:i:1\n",
        "SOLEXA-1GA-1_1_FC20EMA:7:138:519:904\t0\tchr1\t169885358\t25\t27M\t*\t0\t0\tTGTGTGGGTGTGTGGCGGGGGGGGGTG\t]hQ[Th`QgWGVDhOHR[ICI@G@NUH\tX1:i:1\tMD:Z:6G20\tNM:i:1\n",
        "SOLEXA-1GA-1_4_FC20ENL:7:215:509:395\t16\tchr1\t169892107\t25\t27M\t*\t0\t0\tTGTGTGAATGCGCGTGTGTGTTTGTGT\tEIEKBKLHDLOINRMTMQLLLQX]XS^\tX1:i:1\tMD:Z:14C12\tNM:i:1\n",
        "SOLEXA-1GA-1_4_FC20ENL:7:39:938:359\t16\tchr1\t169797741\t25\t27M\t*\t0\t0\tGTGCAGTGGCATGACCATGGGTCACTG\tGERELNLHNKWLROR^VPNUYXT[hTc\tX0:i:1\tMD:Z:27\tNM:i:0\n",
        "SOLEXA-1GA-1_4_FC20ENL:7:266:936:393\t0\tchr1\t169821660\t25\t27M\t*\t0\t0\tACTTCTCATTGGGAGCCCTTTGGGACA\thhhhhhhhhhhhhhghhhhhhhYhhhh\tX0:i:1\tMD:Z:27\tNM:i:0\n",
        "SOLEXA-1GA-1_4_FC20ENL:7:1:838:683\t0\tchr1\t169844997\t25\t27M\t*\t0\t0\tAAAAAATTCAAGTCTCCAAATCTAAGA\tXLNQNLOKGERPJGHEAKDBNGNFFCG\tX0:i:1\tMD:Z:27\tNM:i:0\n",
        "SOLEXA-1GA-1_4_FC20ENL:7:178:334:611\t16\tchr1\t169852422\t25\t27M\t*\t0\t0\tGAGGGTAAGGCTCGCGTGCCCACTGCC\tEA@HBDFF?FG>HDEGDEJKKIYLSN_\tX1:i:1\tMD:Z:18G5G2\tNM:i:1\n"};

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNonExistentFile() throws Exception {
        BAMParser parser = new BAMParser(pathToNonExistentFile, new BEDParser(pathToBEDFile).parse());
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToFileWithWrongExtension() throws Exception {
        BAMParser parser = new BAMParser(pathToFileWithWrongExt, new BEDParser(pathToBEDFile).parse());
    }

    @Test
    public void CreationFromPathToCorrectFile() throws Exception {
        BAMParser parser = new BAMParser(pathToCorrectFile, new BEDParser(pathToBEDFile).parse());
    }

    @Test(expected = GenomeFileException.class)
    public void CreationFromPathToNotAFile() throws Exception {
        BAMParser parser = new BAMParser(pathToNotAFile, new BEDParser(pathToBEDFile).parse());
    }

    @Test(expected = GenomeException.class)
    public void ParsingIncorrectFile() throws Exception {
        BAMParser parser = new BAMParser(pathToIncorrectFile, new BEDParser(pathToBEDFile).parse());
        parser.parse();
    }

    @Test
    public void ParsingCorrectFile() throws Exception {
        BAMParser parser = new BAMParser(pathToCorrectFile, new BEDParser(pathToBEDFile).parse());
        ArrayList<SAMRecord> samRecords = parser.parse();
        for (int i = 0; i < samRecords.size(); i++) {
            assertEquals(samRecords.get(i).getSAMString(), checkArray[i]);
        }
    }
}
