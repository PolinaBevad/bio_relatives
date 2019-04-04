package genome.assembly;

import htsjdk.samtools.SAMRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
     * Enum of different nucleotides that may occur in the genome.
     */
    private enum Nucleotides
    {
        A {
            @Override
            public String toString()
                {
                    return "a";
                }
        }, // adenine
        G {
            @Override
            public String toString()
                {
                    return "g";
                }
        }, // thymine
        T {
            @Override
            public String toString()
                {
                    return "c";
                }
        }, // guanine
        C {
            @Override
            public String toString()
                {
                    return "t";
                }
        } // cytosine
    }

    @Override
    public List<GenomeRegion> assembly()
    {
        return new ArrayList<GenomeRegion>();
    }

    /**
     * Checks if the current position is in the range [start position of the
     * nucleotide; start position + nucleotide sequence len]. Using this method we
     * check, if the {@link htsjdk.samtools.SAMRecord} contains current processing nucleotide
     * @param position Position of the current nucleotide.
     * @param start Start position of the nucleotide in the {@link htsjdk.samtools.SAMRecord}
     * @param end End psoition of the nucleotide sequence.
     * @return True, if position is in range [start, start + len]. False otherwise.
     */
    private static boolean inRange(int position, int start, int end)
    {
        return position >= start  && position <= end;
    }

    /**
     * Generates a map with each nucleotide and it's quality for the further usage.
     * @param reads Array of {@link SAMRecord} read from BAM file.
     * @param position Current position of the nucleotide we are analyzing.
     * @return HashMap with qualities for this nucleotide.
     */
    private HashMap<Character, ArrayList<Byte>> getNucleotideDistribution(ArrayList<SAMRecord> reads, int position)
    {
        // initialize the storing structure
        HashMap<Character, ArrayList<Byte>> dist = new HashMap<>();
        for (char c : NUCLEOTIDES.toCharArray())
        {
            dist.put(c, new ArrayList<>());
        }
        // for each read get the
        // nucleotide and it's quality if it contains it
        reads.forEach(s ->
        {
            if (!inRange(position, s.getStart(), s.getEnd()))
            {
                int pos = position - s.getStart();
                char n = s.getReadString().toLowerCase().charAt(pos);
                byte q = s.getBaseQualities()[pos];
                dist.get(n).add(q);
            }
        });
        return dist;
    }
}
