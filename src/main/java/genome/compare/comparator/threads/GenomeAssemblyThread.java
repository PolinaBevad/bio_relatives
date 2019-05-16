package genome.compare.comparator.threads;

import bam.BAMParser;
import bam.BEDFeature;
import exception.GenomeException;
import exception.GenomeThreadException;
import genome.assembly.GenomeConstructor;
import genome.assembly.GenomeRegion;
import htsjdk.samtools.SAMRecord;
import util.LinkedSAMRecordList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Khvatov
 */
public class GenomeAssemblyThread implements Runnable {

    /**
     * Corresponding BAM file parser.
     */
    private final BAMParser bamFile;

    /**
     * BED file feature.
     */
    private final BEDFeature feature;

    /**
     * list, that is used to store the results
     */
    private final List<GenomeRegion> personGenome;

    /**
     * Creates the {@link GenomeAssemblyThread} from the corresponding BAM file parser,
     * bed file feature and the list, that is used to store the results.
     *
     * @param parser  Corresponding BAM file parser
     * @param feature BED file feature
     * @param genome  list, that is used to store the results
     * @throws GenomeException if genome is not empty.
     */
    public GenomeAssemblyThread(BAMParser parser, BEDFeature feature, List<GenomeRegion> genome) throws GenomeException {
        this.bamFile = parser;
        this.feature = feature;

        if (!genome.isEmpty()) {
            throw new GenomeException("GenomeAssemblyThread", "genome", "is not empty");
        }
        this.personGenome = genome;
    }

    /**
     * run() method of the interface {@link Runnable} implementation.
     * Processes each feature. Firstly, parses the corresponding
     * BAM file and get the {@link htsjdk.samtools.SAMRecord} objects from
     * it. Than, using the corresponding BED feature assemblies the
     * genome into List of genome regions, that are then stored in the result
     * list.
     */
    @Override
    public void run() {
        try {
            // get the list of sam records for each person
            LinkedSAMRecordList samRecords = bamFile.parse(feature);
            // construct genomes for this gene
            personGenome.addAll(GenomeConstructor.assembly(samRecords, feature));
        } catch (GenomeException gex) {
            throw new GenomeThreadException(gex.getMessage());
        }
    }
}
