package genome.assembly;

import bam.BAMParser;
import bam.BEDParser;
import exception.*;
import htsjdk.samtools.SAMRecord;

import java.util.*;

/**
 * This class implements {@link GenomeAssembler} interface. It is designed to parse the
 * input BAM file and generate / construct the genome that is stored in this BAM file
 * according to the qualities of each nucleotide.
 *
 * @author Vladislav Marchenko
 * @author Sergey Khvatov
 */
public class GenomeConstructor implements GenomeAssembler
{
    /**
     * String of the nucleotides.
     */
    private static final String NUCLEOTIDES = "agct";

    /**
     * input ArrayList of SamRecords from which we will construct a genome
     */
    public ArrayList<SAMRecord> samRecords;

    /**
     * input ArrayList of genome regions from BED file
     */
    public ArrayList<BEDParser.BEDFeature> exons;

    /**
     * Constructor of class GenomeConstructor from samRecords and exons
     * @param samRecords input ArrayList of SamRecords from which we will construct a genome
     * @param exons input ArrayList of genome regions from BED file
     * @throws InvalidGenomeConstructorException if input data is empty
     */
    public GenomeConstructor(ArrayList<SAMRecord> samRecords, ArrayList<BEDParser.BEDFeature> exons) throws InvalidGenomeConstructorException {
        this.samRecords = samRecords;
        if (samRecords.isEmpty()) {
            throw new InvalidGenomeConstructorException("GenomeConstructor", "samRecords", "empty");
        }

        this.exons = exons;
        if (exons.isEmpty()) {
            throw new InvalidGenomeConstructorException("GenomeConstructor", "exons", "empty");
        }
    }

    /**
     * Constructor of class GenomeConstructor from BAM and BED files
     * @param BAMFileName name of input BAM file
     * @param BEDFileName name of input BED file
     * @throws InvalidGenomeConstructorException if input files are invalid
     */
    public GenomeConstructor(String BAMFileName, String BEDFileName) throws InvalidGenomeConstructorException {
        try {
            this.exons = new BEDParser(BEDFileName).parse();
            this.samRecords = new BAMParser(BAMFileName, new BEDParser(BEDFileName).parse()).parse();
        }
        catch (InvalidBAMFileException | InvalidBEDFileException ex) {
            // if catch an exception then create our InvalidGenomeAssemblyException exception,
            InvalidGenomeConstructorException ibfex = new InvalidGenomeConstructorException(ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }
    }

    /**
     * Assembly a genome from SAMRecords
     * @return ArrayList of GenomeRegions(output genome)
     * @throws InvalidGenomeAssemblyException if errors occur
     */
    @Override
    public List<GenomeRegion> assembly() throws InvalidGenomeAssemblyException {
        try {
            // output genome
            List<GenomeRegion> genomeRegions = new ArrayList<>();
            // we pass through each region from the BED file(each exon)
            for (int i = 0; i < exons.size(); i++) {

                // array of qualities of nucleotides from the current region
                byte [] qualities = new byte[exons.get(i).getEndPos() - exons.get(i).getStartPos()];
                // String of nucleotides from the current region
                StringBuilder nucleotides = new StringBuilder();

                // we pass from start position to end position of current region
                for (int j = exons.get(i).getStartPos(); j < exons.get(i).getEndPos(); j++) {
                    // HashMap in which there are nucleotides(with their qualities; see description of the method)
                    // from current position
                    HashMap<Character, ArrayList<Byte>> currentNucleotides = getNucleotideDistribution(j);

                    // the best nucleotide(if there are not any nucleotides, we write a *)
                    char bestNucleotide = '*';
                    // the best median quality of nucleotide
                    byte bestQuality = 0;
                    // the best count of nucleotides
                    int bestCount = 0;

                    // we pass on HashMap ( we define the best nucleotide)
                    Set<Map.Entry<Character, ArrayList<Byte>>> set = currentNucleotides.entrySet();
                    for (Map.Entry<Character, ArrayList<Byte>> s : set) {
                        // if the current nucleotide is the most met then it is the best nucleotide
                        if (s.getValue().size() > bestCount) {
                            bestCount = s.getValue().size();
                            bestNucleotide = s.getKey();
                            bestQuality = getMedianQuality(s.getValue());
                        }

                        // if the current nucleotide occurs as many times as the best, then we look at their quality
                        if (s.getValue().size() == bestCount) {
                            // if the current nucleotide has the best quality, then it is the best
                            if (getMedianQuality(s.getValue()) > bestQuality) {
                                bestCount = s.getValue().size();
                                bestNucleotide = s.getKey();
                                bestQuality = getMedianQuality(s.getValue());
                            }
                        }
                    }

                    // add the best nucleotide into the nucleotide siquence from the current region
                    nucleotides.append(bestNucleotide);
                    // add the quality of this nucleotide
                    qualities[j - exons.get(i).getStartPos()] = bestQuality;
                }

                // add the current region into output List
                genomeRegions.add(new GenomeRegion(exons.get(i).getChromosomeName(), exons.get(i).getStartPos(),
                        nucleotides.toString().toUpperCase(), qualities));
            }
            return genomeRegions;
        } catch (NullPointerException | InvalidRegionException | IllegalArgumentException ex ) {
            // if catch an exception then create our InvalidGenomeAssemblyException exception,
            InvalidGenomeAssemblyException ibfex = new InvalidGenomeAssemblyException(ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }
    }

    /**
     * Checks if the current position is in the range [start position of the
     * nucleotide; start position + nucleotide sequence len]. Using this method we
     * check, if the {@link SAMRecord} contains current processing nucleotide
     * @param position Position of the current nucleotide.
     * @param start Start position of the nucleotide in the {@link SAMRecord}
     * @param end End psoition of the nucleotide sequence.
     * @return True, if position is in range [start, start + len]. False otherwise.
     */
    private static boolean inRange(int position, int start, int end)
    {
        return position >= start  && position <= end;
    }

    /**
     * Generates a map with each nucleotide and it's quality for the further usage.
     * @param position Current position of the nucleotide we are analyzing.
     * @return HashMap with qualities for this nucleotide.
     */
    private HashMap<Character, ArrayList<Byte>> getNucleotideDistribution(int position)
    {
        // initialize the storing structure
        HashMap<Character, ArrayList<Byte>> dist = new HashMap<>();
        for (char c : NUCLEOTIDES.toCharArray())
        {
            dist.put(c, new ArrayList<>());
        }
        // for each read get the
        // nucleotide and it's quality if it contains it
        samRecords.forEach(s ->
        {
            if (inRange(position, s.getStart(), s.getEnd()))
            {
                int pos = position - s.getStart();
                char n = s.getReadString().toLowerCase().charAt(pos);
                byte q = s.getBaseQualities()[pos];
                dist.get(n).add(q);
            }
        });
        return dist;
    }

    /**
     * method for definition of median quality of the nucleotide
     * @param qualities input array of qualities of the nucleotide
     * @return median quality of the nucleotide
     */
    private byte getMedianQuality (ArrayList<Byte> qualities) {
        // sort ArrayList of qualities
        Collections.sort(qualities);

        // if the number of elements is odd, then we take the middle element
        if (qualities.size() % 2 != 0) {
            return qualities.get(qualities.size() / 2);
        }

        // else return half of the sum of the two middle elements of the array
        else if (!qualities.isEmpty()){
            return (byte) ((qualities.get(qualities.size() / 2) + qualities.get(qualities.size() / 2 - 1)) / 2);
        }
        // if there are not any nucleotides, return 0
        else {
            return 0;
        }
    }
}
