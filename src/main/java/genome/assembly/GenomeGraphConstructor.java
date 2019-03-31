package genome.assembly;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a class, that implements effective nucleotide
 * assembly using De Bruijn graph.
 *
 * @author Sergey Khvatov
 */
public class GenomeGraphConstructor implements GenomeAssembler
{
    @Override
    public List<Nucleotide> assembly()
    {
        return new ArrayList<>();
    }
}
