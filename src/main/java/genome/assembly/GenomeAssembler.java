package genome.assembly;

import exception.InvalidGenomeAssemblyException;

import java.util.List;

/**
 * This interface defines a basic functional interface for
 * different genome assembling algorithms.
 * @author Vladislav Marchenko
 * @author Sergey Khvatov
 */
public interface GenomeAssembler
{
    List<GenomeRegion> assembly() throws InvalidGenomeAssemblyException;
}
