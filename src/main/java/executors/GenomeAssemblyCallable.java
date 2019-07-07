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

package executors;

import bam.regular.BAMParser;
import bam.regular.BEDFeature;
import exception.GenomeException;
import exception.GenomeFileException;
import genome.assembly.GenomeConstructor;
import genome.assembly.GenomeRegion;
import genome.assembly.SAMRecordList;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * {@link GenomeAssemblyCallable} class implements a {@link Callable} interface.
 * Overrides the call() method, so that it assemblies the genome region nucleotide sequence
 * that represents this region in the BED file.x`
 *
 * @author Sergey Khvatov
 */
public class GenomeAssemblyCallable implements Callable<List<GenomeRegion>> {

    /**
     * Corresponding BAM file parser.
     */
    private BAMParser bamFile;

    /**
     * BED file feature.
     */
    private BEDFeature feature;

    /**
     * Creates the {@link GenomeAssemblyCallable} from the corresponding BAM file parser
     * and bed file feature that represents this region.
     *
     * @param parser  Corresponding BAM file parser.
     * @param feature BED file feature.
     */
    public GenomeAssemblyCallable(BAMParser parser, BEDFeature feature) {
        this.bamFile = parser;
        this.feature = feature;
    }

    /**
     * {@link Callable} interface method call() override.
     * Assemblies the genome region nucleotide sequence
     * from input BAM file parser and BED file feature,
     * that represents this region in the BED file.
     *
     * @return List of the assembled {@link GenomeRegion} objects.
     * @throws GenomeException if regions don't pass the validation.
     */
    @Override
    public List<GenomeRegion> call() throws GenomeException, GenomeFileException {
        // getSAMRecordList the list of sam records for each person
        //SAMRecordList samRecords = new BAMParser(this.bamFile.getBAMFileName()).parse(feature);
        SAMRecordList samRecords = this.bamFile.parse(feature);
        // assembly the nucleotides and return the result
        return GenomeConstructor.assembly(samRecords, feature);
    }
}
