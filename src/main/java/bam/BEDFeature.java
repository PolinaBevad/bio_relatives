package bam;

import exception.GenomeException;

import java.util.HashSet;

/**
 * BED file record class.
 *
 * @author Sergey Khvatov
 */
public class BEDFeature {

    /**
     * Regular expression that contains all
     * the allowed symbols for the gene name.
     */
    private static final String ALLOWED_SYMBOLS_REGEXP = "[a-zA-Z0-9.\\-_+]*";

    /**
     * Name of the chromosome.
     */
    private String chrom;

    /**
     * Start position of the feature.
     */
    private int start;

    /**
     * End position of the feature.
     */
    private int end;

    /**
     * Name of the gene that is stored in this record.
     */
    private String gene;

    /**
     * Default class constructor from chromosome name, start and positions of the feature.
     *
     * @param chrom Name of the chromosome.
     * @param start Start position.
     * @param end   End position.
     * @param gene  Name of the gene.
     * @throws GenomeException if start or end positions are incorrect.
     */
    public BEDFeature(String chrom, int start, int end, String gene) throws GenomeException {
        this.chrom = chrom;
        if (start <= 0 || end <= 0 || start >= end) {
            throw new GenomeException("Error occurred during initialization of BEDFeature object: " + "Incorrect parameters were passed: [" + chrom + ", " + start + ", " + end + ", " + gene);
        }

        this.gene = gene;
        if (!gene.matches(ALLOWED_SYMBOLS_REGEXP)) {
            throw new GenomeException("Error occurred during initialization of BEDFeature object: " + "Incorrect parameters were passed: [" + chrom + ", " + start + ", " + end + ", " + gene);
        }

        this.start = start;
        this.end = end;
    }

    /**
     * Get the nam of the chromosome method.
     *
     * @return Name of the chromosome.
     */
    public String getChromosomeName() {
        return chrom;
    }

    /**
     * Get the start position method.
     *
     * @return Start position of the feature in the chromosome.
     */
    public int getStartPos() {
        return start;
    }

    /**
     * Get the end position method.
     *
     * @return End position of the feature in the chromosome.
     */
    public int getEndPos() {
        return end;
    }

    /**
     * @return name of the gene in this record.
     */
    public String getGene() {
        return gene;
    }
}