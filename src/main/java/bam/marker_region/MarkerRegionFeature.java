package bam.marker_region;

import bam.regular.BEDFeature;
import exception.GenomeFileException;

import java.util.regex.Pattern;

/**
 * marker region file record class.
 *
 * @author Sergey Khvatov
 */
public class MarkerRegionFeature extends BEDFeature {

    /**
     * Default value for the 'gene', which contains this marker region
     * when we are in the X_STR mode.
     */
    private static final String X_STR = "X_STR";

    /**
     * Default value for the 'gene', which contains this marker region
     * when we are in the Y_STR mode.
     */
    private static final String Y_STR = "Y_STR";

    /**
     * Name of the marker.
     */
    private String markerName;

    /**
     * Repeat motif of the STR.
     */
    private Pattern repeatMotif;

    /**
     * Creates an instance of the marker region record.
     *
     * @param chrom      Name of the chromosome.
     * @param start      Start position of the region.
     * @param end        End position.
     * @param markerName Name of the marker.
     * @param motif      Nucleotide sequence which represents this marker.
     * @throws GenomeFileException if invalid input occurs.
     */
    public MarkerRegionFeature(String chrom, int start, int end, String markerName, Pattern motif) {
        super(chrom, start, end, chrom.contains("Y") ? Y_STR : X_STR);
        this.markerName = markerName;
        this.repeatMotif = motif;
    }

    /**
     * @return name of the marker.
     */
    public String getMarkerName() {
        return markerName;
    }

    /**
     * @return repeat motif value.
     */
    public Pattern getRepeatMotif() {
        return repeatMotif;
    }
}
