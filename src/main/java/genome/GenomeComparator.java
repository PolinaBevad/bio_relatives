package genome;

/**
 * Defines the class, that compares and analyses the genomes of two people,
 * using {@link} algorithm.
 *
 * @author Sergey Hvatov
 * @author Vladislav Marchenko
 */
public class GenomeComparator
{
    /**
     * Genome of the first person.
     */
    private String firstPersonGenome_;

    /**
     * Genome of the second person.
     */
    private String secondPersonGenome_;

    /**
     * Default class constructor from genomes of two people.
     *
     * @param first  Genome of the first person.
     * @param second Genome of the second person.
     */
    public GenomeComparator(String first, String second)
    {
        this.firstPersonGenome_ = first;
        this.secondPersonGenome_ = second;
    }

    /**
     * Compares two genomes.
     *
     * @return The percentage of the similarity between two genomes.
     */
    public double comparePercentage()
    {
        return 0;
    }
}
