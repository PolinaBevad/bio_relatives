package genome.compare;

import bam.BAMParser;
import bam.BEDParser;
import genome.assembly.GenomeConstructor;
import htsjdk.samtools.SAMRecord;
import org.junit.Test;

import java.util.ArrayList;


/**
 * Tests for the {@link Person} class.
 *
 * @author Vladislav Marchenko
 */
public class PersonTest {
    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_1 = "src/test/resources/genome/compare/testMotherX.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_SON_BAM_1 = "src/test/resources/genome/compare/testSonX.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_2 = "src/test/resources/genome/compare/testMotherMT.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_SON_BAM_2 = "src/test/resources/genome/compare/testSonMT.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_DAD_BAM_2 = "src/test/resources/genome/compare/testDadMT.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_DAD_BAM_1 = "src/test/resources/genome/compare/testDadX.bam";

    /**
     * Path to the second test BAM file
     */
    private final static  String PATH_TO_DAD_BAM_3 = "src/test/resources/genome/compare/testDad4.bam";

    /**
     * Path to the first test BAM file
     */
    private final static  String PATH_TO_MOM_BAM_3 = "src/test/resources/genome/compare/testMother4.bam";

    /**
     * Path to the first test BED file
     */
    private final static  String PATH_TO_BED = "src/test/resources/genome/compare/correct2.bed";

    @Test
    public void GenomeComparisonOfNotParentAndChild() throws Exception {
        BEDParser bedParser = new BEDParser(PATH_TO_BED);
        BAMParser bamParser = new BAMParser(PATH_TO_DAD_BAM_2, bedParser.parse());
        ArrayList<SAMRecord> bam = bamParser.parse();
        ArrayList<BEDParser.BEDFeature> bed = bedParser.parse();
        GenomeConstructor c = new GenomeConstructor(bam, bed);
        Person son = new Person(
                c.assembly()
        );
        Person mother = new Person(
                new GenomeConstructor(new BAMParser(PATH_TO_MOM_BAM_2, bedParser.parse()).parse(),bedParser.parse()).assembly()

        );
        System.out.println(son.compareGenomes(mother).toString());
    }
}
