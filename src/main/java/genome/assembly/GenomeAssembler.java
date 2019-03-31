package genome.assembly;

import java.util.List;

/**
 * This interface defines a basic functional interface for
 * different genome assembling algorithms.
 *
 * @author Sergey Khvatov
 */
public interface GenomeAssembler
{
    List<Nucleotide> assembly();
}
