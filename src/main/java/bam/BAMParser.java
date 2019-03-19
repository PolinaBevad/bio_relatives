package bam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import htsjdk.samtools.SAMRecord;
import htsjdk.tribble.bed.FullBEDFeature;

/**
 * Class for parsing of BAM files to ArrayList of SAMRecords.
 *
 * @author Vladislav Marchenko
 */
public class BAMParser
{
    /**
     * BAM file.
     */
    private File BAMFile;

    /**
     * Name of BAM file.
     */
    private String BAMFileName;

    /**
     * List of exons for which BAM file is parsed.
     */
    private List<FullBEDFeature.Exon> exons;

    /**
     * Output ArrayList of SAMRecords.
     */
    private ArrayList<SAMRecord> samRecords;

    /**
     * Output byte array of qualities of each output SAMRecord.
     */
    private byte[] qualities;

    /**
     * Deafult class constructor from BED file and list of exons.
     *
     * @param BEDFile BED file.
     * @param exons   List of exons from the BED file.
     */
    public BAMParser(File BEDFile, List<FullBEDFeature.Exon> exons)
    {
        this.BAMFile = BEDFile;
        this.BAMFileName = BEDFile.getName();
        this.exons = exons;
    }

    /**
     * Deafult class constructor from BED file and list of exons.
     *
     * @param BEDFileName name of the BED file.
     * @param exons       List of exons from the BED file.
     */
    public BAMParser(String BEDFileName, List<FullBEDFeature.Exon> exons)
    {
        this.BAMFileName = BEDFileName;
        this.BAMFile = new File(BEDFileName);
        this.exons = exons;
    }

    /**
     * Validates the input BED file.
     *
     * @return false if BAM file is not valid, else return true.
     */
    public static boolean isValid()
    {
        return true;
    }

    /**
     * Convert BAM file to SAM file method.
     */
    public void convertToSAMFile()
    {

    }

    /**
     * Parse BAM/SAM file method.
     * Takes an ArrayList of SAMRecords and byte array with quality of each region of genome(SAMRecord)
     */
    public void parse()
    {

    }

    /**
     * Get the list of SAM records method.
     *
     * @return ArrayList of SAMRecords
     */
    public ArrayList<SAMRecord> getSamRecords()
    {
        return samRecords;
    }

    /**
     * Get the qualities of each region method.
     *
     * @return byte array with quality of each region of genome(SAMRecord)
     */
    public byte[] getQualities()
    {
        return qualities;
    }

}
