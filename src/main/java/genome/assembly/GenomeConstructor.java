/**
 * MIT License
 *
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package genome.assembly;

import bam.regular.BEDFeature;
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

    /**
     * Maximum percent of the unknown nucleotides in the sequence.
     */
    private static final double UNKNOWN_NUCL_PERCENTAGE = 1.1d;

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
     * @param samRecords input SAMRecordList
     * @param exons input ArrayList of exones
     * @return gene (List of GenomeRegion)
     * @throws GenomeException if anything went wrong.
     */
    public static List<GenomeRegion> assembly(SAMRecordList samRecords, List<BEDFeature> exons) {
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
     * @param samRecords input SAMRecordList
     * @param exon input exon
     * @return gene (List of GenomeRegion)
     * @throws GenomeException if anything went wrong
     */
    public static List<GenomeRegion> assembly(SAMRecordList samRecords, BEDFeature exon) {
        try {
            // check the input
            if (samRecords.isEmpty()) {
                //throw new GenomeException("GenomeConstructor", "assembly", "samRecords", "is empty");
                return new ArrayList<>();
            }

            // list of regions
            List<GenomeRegion> genomeRegions = new ArrayList<>();
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
                Map<Character, List<Byte>> currentNucleotides = getNucleotideDistribution(samRecords.getSAMRecordList(j), j);
                // the best nucleotide(if there are not any nucleotides, we write a *)
                char bestNucleotide = UNKNOWN_NUCLEOTIDE;
                // the best median quality of nucleotide
                byte bestQuality = 0;
                // the best count of nucleotides
                int bestCount = 0;

                // we pass on HashMap ( we define the best nucleotide)
                Set<Map.Entry<Character, List<Byte>>> set = currentNucleotides.entrySet();
                for (Map.Entry<Character, List<Byte>> s : set) {
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
     * Generates a map with each nucleotide and it's quality for the further usage.
     *
     * @param samRecords List of SAMRecords with the position
     * @param position  Current position of the nucleotide we are analyzing.
     * @return HashMap with qualities for this nucleotide.
     */
    private static Map<Character, List<Byte>> getNucleotideDistribution(List<SAMRecord> samRecords, int position) {
        // initialize the storing structure
        Map<Character, List<Byte>> dist = new HashMap<>();
        for (char c : NUCLEOTIDES.toCharArray()) {
            dist.put(c, new ArrayList<>());
        }

        // for each read getSAMRecordList the
        // nucleotide and it's quality if it contains it
        for (SAMRecord s : samRecords) {
            int pos = position - s.getStart();
            char n = ' ';
            byte q = 0;
            if (pos < s.getReadLength() && pos >= 0) {
                n = Character.toLowerCase(s.getReadString().charAt(pos));
                q = s.getBaseQualities()[pos];
            }
            if (NUCLEOTIDES.contains(String.valueOf(n))) {
                dist.get(n).add(q);
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
    private static byte getMedianQuality(List<Byte> qualities) {
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
}