package genome.compare.comparator;

import bam.BAMParser;
import bam.BEDFeature;
import bam.BEDParser;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.assembly.GenomeConstructor;
import genome.assembly.GenomeRegion;
import genome.compare.analyzis.GeneComparisonResult;
import htsjdk.samtools.SAMRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private HashMap<String, ArrayList<BEDFeature>> exons;

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
     *
     * @return Results of the comparison of two genomes - hashmap, where
     * Key - name of the gene,
     * Value - List of the results of comparison of two genes from this chromosome,.
     * @throws GenomeException if exception occurs while parsing the input files.
     */
    public HashMap<String, ArrayList<GeneComparisonResult>> compareGenomes() throws GenomeException {
        /*
         * Results of the comparison of two genomes - hashmap, where
         * Key - name of the gene,
         * Value - List of the results of comparison of two genes from this chromosome,.
         */
        HashMap<String, ArrayList<GeneComparisonResult>> comparisonResults = new HashMap<>();
        for (String gene : exons.keySet()) {
            // get the list of the exons that contains this gene
            ArrayList<BEDFeature> features = exons.get(gene);
            for (BEDFeature feature : features) {
                // get the list of sam records for each person
                ArrayList<SAMRecord> firstSamRecords = firstBAMFile.parse(feature);
                ArrayList<SAMRecord> secondSamRecords = secondBAMFile.parse(feature);

                // construct genomes for this gene
                List<GenomeRegion> firstPersonsGenome = GenomeConstructor.assembly(firstSamRecords, feature);
                List<GenomeRegion> secondPersonsGenome = GenomeConstructor.assembly(secondSamRecords, feature);

                // check the results
                if (firstPersonsGenome.size() != secondPersonsGenome.size()) {
                    throw new GenomeException(this.getClass().getName(), "compareGenomes", "sizes of regions are different");
                }

                // temporary list that will store the result
                ArrayList<GeneComparisonResult> tempResult = new ArrayList<>();
                for (int i = 0; i < firstPersonsGenome.size(); i++) {
                    GenomeRegionComparator comparator = new GenomeRegionComparator(firstPersonsGenome.get(i), secondPersonsGenome.get(i));
                    tempResult.add(comparator.LevenshteinDistance());
                }
                if (comparisonResults.containsKey(gene)) {
                    comparisonResults.get(gene).addAll(tempResult);
                }
                else {
                    comparisonResults.put(gene, tempResult);
                }
            }
        }
        return comparisonResults;
    }
}
