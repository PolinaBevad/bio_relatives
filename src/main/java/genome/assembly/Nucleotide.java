package genome.assembly;

import exception.InvalidNucleotideException;

/**
 * This class represents a basic data structure to store the data about
 * nucleotides parsed from SAM / BAM files and stored in the {@link htsjdk.samtools.SAMRecord}
 * after genome assembly.
 *
 * @author Sergey Khvatov
 */
public class Nucleotide
{
    /**
     * Name of the chromosome, which contains this nucleotide.
     */
    private String chrom_;

     /**
      * The gene from chromosome.
      */
    private String gene_;

    /**
     * Nucleotide read.
     */
    private String nucleotide_;

    /**
     * Start position of this nucleotide in the genome.
     */
    private int startPos_;

    /**
     * Quality of this read.
     */
    private double quality_;

    /**
     * Default {@link Nucleotide} class constructor from basic information about nucleotide.
     * @param chrom Name of the chromosome.
     * @param gene Name of the gene.
     * @param nucleotide Nucleotide read.
     * @param pos Position in the genome.
     * @param q Quality of this read.
     * @throws InvalidNucleotideException if input data is incorrect.
     */
    public Nucleotide(String chrom, String gene, String nucleotide, int pos, double q) throws InvalidNucleotideException
    {
        // check input data
        if (pos < 0)
            throw new InvalidNucleotideException("Error occurred while creating Nucleotide object: [pos] < 0.");
        if (q <= 0 || q > 1)
            throw new InvalidNucleotideException("Error occurred while creating Nucleotide object: [q] must be in range (0; 1]");

        // init fields if everything is ok
        this.chrom_ = chrom;
        this.gene_ = gene;
        this.nucleotide_ = nucleotide;
        this.startPos_ = pos;
        this.quality_ = q;
    }

    public String getChrom()
    {
        return chrom_;
    }

    public String getGene()
    {
        return gene_;
    }

    public String getNucleotide()
    {
        return nucleotide_;
    }

    public int getStartPos()
    {
        return startPos_;
    }

    public double getQuality()
    {
        return quality_;
    }
}
