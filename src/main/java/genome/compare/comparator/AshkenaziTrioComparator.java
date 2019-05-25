package genome.compare.comparator;

import exception.GenomeException;
import exception.GenomeFileException;
import genome.compare.comparator.threads.GenomeComparatorThread;

/**
 * Class for running of genome comparing of two or three persons
 * @author Vladislav Marchenko
 */
public class AshkenaziTrioComparator {

    /**
     * Static method , which run a comparing of genomes of two persons
     * @param BAMFileName1       name of the BAM file which contains genome of the 1st person
     * @param BAMFileName2       name of the BAM file which contains genome of the 2nd person
     * @param BEDFileName        name of the BED file
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     * @throws GenomeFileException if some errors of input files occurred
     * @throws GenomeException if some errors occurred through the work of code
     * @return String with results of comparing of two genomes
     */
    public static String compareTwoGenomes(String BAMFileName1, String BAMFileName2,String BEDFileName,  boolean intermediateOutput) throws GenomeFileException, GenomeException {
        GenomeComparatorThread comparator = new GenomeComparatorThread(BAMFileName1, BAMFileName2, BEDFileName);
        return comparator.compareGenomes(intermediateOutput).toString();
    }

    /**
     * Static method , which run a comparing of genomes of three persons: son with father and son with mother
     * @param fatherBAMFileName  name of the BAM file which contains genome of the father
     * @param motherBAMFileName  name of the BAM file which contains genome of the mother
     * @param sonBAMFileName     name of the BAM file which contains genome of the son
     * @param BEDFileName        name of the BED file
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     * @throws GenomeFileException if some errors of input files occurred
     * @throws GenomeException if some errors occurred through the work of code
     * @return String with results of genomes comparing of three persons: son with father and son with mother
     */
    public static String compareThreeGenomes(String fatherBAMFileName, String motherBAMFileName, String sonBAMFileName,String BEDFileName,  boolean intermediateOutput) throws GenomeFileException, GenomeException {
        GenomeComparatorThread comparator1 = new GenomeComparatorThread(sonBAMFileName, fatherBAMFileName, BEDFileName);
        StringBuilder result = new StringBuilder("Comparison of father and son genomes:\n");
        result.append(comparator1.compareGenomes(intermediateOutput));

        GenomeComparatorThread comparator2 = new GenomeComparatorThread(sonBAMFileName, motherBAMFileName, BEDFileName);
        result.append("\nComparison of mother and son genomes:\n");
        result.append(comparator2.compareGenomes(intermediateOutput));

        return result.toString();
    }
}
