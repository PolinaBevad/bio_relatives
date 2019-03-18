package bam;

import java.io.File;
import java.util.List;

import htsjdk.tribble.bed.FullBEDFeature;

/**
 * Class for getting exons from BED file.
 *
 * @author Vladislav Marchenko
 * @author Sergey Hhatov
 */
public class BEDReader {
    /**
     * nput BED file.
     */
    private File BEDFile;

    /**
     * name of input BED file.
     */
    private String BEDFileName;

    /**
     * Default class constructor from BED file.
     * @param BEDFile BED file to create object from.
     */
    public BEDReader(File BEDFile) {
        this.BEDFile = BEDFile;
        this.BEDFileName = BEDFile.getName();
    }

    /**
     * Default class constructor from BED file.
     * @param BEDFileName filename of the BED file to create object from.
     */
    public BEDReader(String BEDFileName) {
        this.BEDFileName = BEDFileName;
        this.BEDFile = new File(BEDFileName);
    }

    /**
     * Validates the input BED file.
     * @return false if BED file is not valid, else return true
     */
    public static boolean isValid() {
        return true;
    }


    /**
     * Parse exons from BED.
     * @return List of exons from the BED file.
     */
    public List<FullBEDFeature.Exon> getExons() {
        return null;
    }

}
