package genome;

/**
 * Defines the class, that creates a new genome string from the input BAM file.
 * @author Sergey Hvatov
 * @author Vladislav Marchenko
 */
public class GenomeConstructor {
    /**
     * Genome result string.
     */
    private String genome_;

    /**
     * File name of the bam file.
     */
    private String bamFile_;

    /**
     * File name of the bed file.
     */
    private String bedFIle_;

    /**
     * Default class constructor from the names of the input files.
     * @param bamFile Filename of the BAM file.
     * @param bedFile Filename of the BED file.
     */
    public GenomeConstructor(String bamFile, String bedFile)
    {
        this.bamFile_ = bamFile;
        this.bedFIle_ = bedFile;
        this.genome_ = generateGenomeString();
    }

    /**
     * Get the genome string from the input BAM file according to the BED file.
     * @return The genome sequence string.
     */
    public String getGenome()
    {
        return genome_;
    }

    /**
     * Realization method, which using {@link} algorithm generates
     * genome string considering the quality of the input sequence.
     * @return The genome sequence string.
     */
    private String generateGenomeString()
    {
        return "";
    }
}
