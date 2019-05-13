package genome.assembly;

import bam.BEDFeature;
import exception.GenomeException;
import htsjdk.samtools.SAMRecord;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * It is designed to parse the
 * input BAM file and generate / construct the genome that is stored in this BAM file
 * according to the qualities of each nucleotide.
 *
 * @author Vladislav Marchenko
 * @author Sergey Khvatov
 */
public class GenomeConstructor  {
    /**
     * String of the nucleotides.
     */
    private static final String NUCLEOTIDES = "agct";

    private static final double UNKNOWN_NUCL_PERCENTAGE = 0.2d;

    /**
     * Unknown nucleotide symbol.
     */
    private static final char UNKNOWN_NUCLEOTIDE = '*';

    /**
     * Maximum length of the nucleotide sequence to be stored
     * in the genome region.
     */
    private static final int MAX_NUCLEOTIDE_SEQ_LEN = 256;
    /**
     * Method, which assembly genome from samrecords and exons
     * @param samRecords input ArrayList of SAMRecords
     * @param exons input ArrayList of exones
     * @return gene (List of GenomeRegion)
     * @throws GenomeException if anything went wrong
     */
    public static List<GenomeRegion> assembly(ArrayList<SAMRecord> samRecords, ArrayList<BEDFeature> exons) throws GenomeException {
        try {
            // output genome
            List<GenomeRegion> genomeRegions = new ArrayList<>();
            // we pass through each region from the BED file(each exon)
            for (BEDFeature exon : exons) {
                genomeRegions.addAll(assembly(samRecords, exon));
            }
            return genomeRegions;
        } catch (NullPointerException | IllegalArgumentException ex) {
            // if catch an exception then create our InvalidGenomeAssemblyException exception,
            GenomeException ibfex = new GenomeException("GenomeConstructor", "assembly", ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }
    }

    /**
     * Overloaded method assembly()
     * @param samRecords input ArrayList of SAMRecords
     * @param exon input exon
     * @return gene (List of GenomeRegion)
     * @throws GenomeException if anything went wrong
     */
    public static List<GenomeRegion> assembly(ArrayList<SAMRecord> samRecords, BEDFeature exon) throws GenomeException {
        try {
            // check the input
            if (samRecords.isEmpty()) {
                throw new GenomeException("GenomeConstructor", "assembly", "samRecords", "is empty");
            }

            // list of regions
            ArrayList<GenomeRegion> genomeRegions = new ArrayList<>();
            // temp quality array
            byte[] qualities = new byte[MAX_NUCLEOTIDE_SEQ_LEN];
            // String of nucleotides from the current region
            StringBuilder nucleotides = new StringBuilder();
            // start position for each new smaller genome region
            int currentStartPos = exon.getStartPos();

            // temp variables used
            // to calculate the percentage of the
            // unknown nucleotides in the sequence.
            int nucleotideSeqLen = 0;
            int unknownNucleotidesNum = 0;

            // we pass from start position to end position of current region
            for (int j = exon.getStartPos(); j < exon.getEndPos(); j++) {
                // HashMap in which there are nucleotides(with their qualities; see description of the method)
                // from current position
                HashMap<Character, ArrayList<Byte>> currentNucleotides = getNucleotideDistribution(samRecords, j, exon.getChromosomeName());
                // the best nucleotide(if there are not any nucleotides, we write a *)
                char bestNucleotide = UNKNOWN_NUCLEOTIDE;
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

                if (nucleotides.length() < MAX_NUCLEOTIDE_SEQ_LEN) {
                    // add the best nucleotide into the nucleotide sequence from the current region
                    nucleotides.append(bestNucleotide);
                    // add the quality of this nucleotide
                    qualities[j - currentStartPos] = bestQuality;
                } else {
                    // add region to the output
                    genomeRegions.add(new GenomeRegion(exon.getChromosomeName(), currentStartPos, nucleotides.toString().toUpperCase(), qualities, exon.getGene()));
                    // reset temporary values
                    nucleotides.setLength(0);
                    Arrays.fill(qualities, (byte) 0);
                    currentStartPos = j + 1;
                    // add number of the nucleotides and number of the unknown nucleotides
                    nucleotideSeqLen += MAX_NUCLEOTIDE_SEQ_LEN;
                    unknownNucleotidesNum += StringUtils.countMatches(nucleotides, UNKNOWN_NUCLEOTIDE);
                }
            }

            // add last processed region to the output
            genomeRegions.add(new GenomeRegion(exon.getChromosomeName(), currentStartPos, nucleotides.toString().toUpperCase(), Arrays.copyOf(qualities, nucleotides.length()), exon.getGene()));
            // add number of the nucleotides and number of the unknown nucleotides
            nucleotideSeqLen += nucleotides.length();
            unknownNucleotidesNum += StringUtils.countMatches(nucleotides, UNKNOWN_NUCLEOTIDE);

            // check the percentage of the *
            // in nucleotide sequence
            if ((double)unknownNucleotidesNum / nucleotideSeqLen > UNKNOWN_NUCL_PERCENTAGE) {
                throw new GenomeException("GenomeConstructor", "assembly", ">= 20% of the nucleotides are unknown");
            }

            return genomeRegions;
        } catch (NullPointerException | IllegalArgumentException ex) {
            // if catch an exception then create our InvalidGenomeAssemblyException exception,
            GenomeException ibfex = new GenomeException("GenomeConstructor", "assembly", ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }
    }
    /**
     * Checks if the current position is in the range [start position of the
     * nucleotide; start position + nucleotide sequence len]. Using this method we
     * check, if the {@link SAMRecord} contains current processing nucleotide
     *
     * @param position Position of the current nucleotide.
     * @param start    Start position of the nucleotide in the {@link SAMRecord}
     * @param end      End psoition of the nucleotide sequence.
     * @return True, if position is in range [start, start + len]. False otherwise.
     */
    private static boolean inRange(int position, int start, int end) {
        return position >= start && position <= end;
    }

    /**
     * Generates a map with each nucleotide and it's quality for the further usage.
     *
     * @param chromName - name of chromosome
     * @param position  Current position of the nucleotide we are analyzing.
     * @return HashMap with qualities for this nucleotide.
     */
    private static HashMap<Character, ArrayList<Byte>> getNucleotideDistribution (ArrayList<SAMRecord> samRecords, int position, String chromName ) throws GenomeException {
        // initialize the storing structure
        HashMap<Character, ArrayList<Byte>> dist = new HashMap<>();
        for (char c : NUCLEOTIDES.toCharArray()) {
            dist.put(c, new ArrayList<>());
        }
        // for each read get the
        // nucleotide and it's quality if it contains it
        for (SAMRecord s : samRecords) {
            if ((inRange(position, s.getStart(), s.getEnd()))) {
                int pos = position - s.getStart();
                char n = ' ';
                byte q = 0;
                if (pos < s.getReadLength()) {
                    n = s.getReadString().toLowerCase().charAt(pos);
                    q = s.getBaseQualities()[pos];
                }
                if (NUCLEOTIDES.contains(String.valueOf(n))) {
                    dist.get(n).add(q);
                }
            }
        }
        return dist;
    }

    /**
     * method for definition of median quality of the nucleotide
     *
     * @param qualities input array of qualities of the nucleotide
     * @return median quality of the nucleotide
     */
    private static byte getMedianQuality(ArrayList<Byte> qualities) {
        // sort ArrayList of qualities
        Collections.sort(qualities);

        // if the number of elements is odd, then we take the middle element
        if (qualities.size() % 2 != 0) {
            return qualities.get(qualities.size() / 2);
        }

        // else return half of the sum of the two middle elements of the array
        else if (!qualities.isEmpty()) {
            return (byte) ((qualities.get(qualities.size() / 2) + qualities.get(qualities.size() / 2 - 1)) / 2);
        }
        // if there are not any nucleotides, return 0
        else {
            return 0;
        }
    }

    /**
     * Checks if the input nucleotide sequence is empty (consists only of *) or not.
     *
     * @param sequence Nucleotide sequence.
     * @return True, if other than '*' symbols appear, false otherwise.
     */
    private static boolean isNotEmptyNucleotideSequence(String sequence) {
        for (char ch : sequence.toCharArray()) {
            if (ch != UNKNOWN_NUCLEOTIDE) {
                return true;
            }
        }
        return false;
    }
}