package genome.compare.comparator;

import bam.BAMParser;
import bam.BEDFeature;
import bam.BEDParser;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.assembly.GenomeConstructor;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GeneComparisonResult;
import genome.compare.analyzis.GeneComparisonResultAnalyzer;
import util.LinkedSAMRecordList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compares several genomes that are stored in the BAM files
 * and stores the result of the comparison.
 *
 * @author Sergey Hvatov
 */
public class GenomeComparator {

    /**
     * Path to the first person's BAM file.
     */
    private BAMParser firstBAMFile;

    /**
     * Path to the second person's BAM file.
     */
    private BAMParser secondBAMFile;

    /**
     * Map with the exons that
     * are parsed from the input BED file.
     */
    private Map<String, List<BEDFeature>> exons;

    /**
     * Default class constructor from paths to the BAM files and corresponding to them BED file.
     *
     * @param pathToFirstBAM  Path to the BAM file where first person's genome is stored.
     * @param pathToSecondBAM Path to the BAM file where first person's genome is stored.
     * @param pathToBED       Path to the BED file.
     * @throws GenomeException     if exception occurs file parsing the BED file.
     * @throws GenomeFileException if incorrect BED or BAM file is passed.
     */
    public GenomeComparator(String pathToFirstBAM, String pathToSecondBAM, String pathToBED) throws GenomeException, GenomeFileException {
        this.firstBAMFile = new BAMParser(pathToFirstBAM);
        this.secondBAMFile = new BAMParser(pathToSecondBAM);
        this.exons = new BEDParser(pathToBED).parse();
    }

    /**
     * Compares two genomes parsing regions for each gene from the input files.
     * @param intermediateOutput if this flag is true , then interim genome comparison results will be displayed,
     *                           else - only the main chromosome results will be obtained
     * @return Object GeneComparisonResultAnalyzer which contains results of the comparison of two genomes
     * @throws GenomeException if exception occurs while parsing the input files.
     */
    public GeneComparisonResultAnalyzer compareGenomes(boolean intermediateOutput) throws GenomeException {
        /*
         * Results of the comparison of two genomes - hashmap, where
         * Key - name of the gene,
         * Value - List of the results of comparison of two genes from this chromosome,.
         */
        GeneComparisonResultAnalyzer comparisonResults = new GeneComparisonResultAnalyzer();
        for (String gene : exons.keySet()) {
            // get the list of the exons that contains this gene
            List<BEDFeature> features = exons.get(gene);
            for (BEDFeature feature : features) {
                // get the list of sam records for each person
                LinkedSAMRecordList firstSamRecords = firstBAMFile.parse(feature);
                LinkedSAMRecordList secondSamRecords = secondBAMFile.parse(feature);

                // construct genomes for this gene
                List<GenomeRegion> firstPersonsGenome = GenomeConstructor.assembly(firstSamRecords, feature);
                List<GenomeRegion> secondPersonsGenome = GenomeConstructor.assembly(secondSamRecords, feature);

                // check the results
                if (firstPersonsGenome.size() != secondPersonsGenome.size()) {
                    throw new GenomeException(this.getClass().getName(), "compareGenomes", "sizes of regions are different");
                }

                for (int i = 0; i < firstPersonsGenome.size(); i++) {
                    GenomeRegionComparator comparator = new GenomeRegionComparator(firstPersonsGenome.get(i), secondPersonsGenome.get(i));
                    GeneComparisonResult geneComparisonResult = comparator.LevenshteinDistance();
                    if (intermediateOutput) {
                        System.out.println(geneComparisonResult);
                    }
                    comparisonResults.add(geneComparisonResult);
                }

            }
        }
        return comparisonResults;
    }
}
