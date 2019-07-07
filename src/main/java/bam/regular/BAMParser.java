/**
 * MIT License
 * <p>
 * Copyright (c) 2019-present Polina Bevad, Sergey Hvatov, Vladislav Marchenko
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bam.regular;

import exception.GenomeException;
import exception.GenomeFileException;
import genome.assembly.SAMRecordList;
import htsjdk.samtools.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class for parsing of BAM files to ArrayList of SAMRecords.
 *
 * @author Vladislav Marchenko
 * @author Sergey Khvatov
 */
public class BAMParser {

    /**
     * Maximum len of the region that will be processed.
     */
    private static final int MAX_REGION_LENGTH = 20;

    /**
     * Default extension of BAM files.
     */
    private static final String BAM_EXTENSION = "bam";

    /**
     * BAM file.
     */
    private File BAMFile;

    /**
     * Default class constructor from name of the BAM file and ArrayList of exons(class BEDFeature).
     *
     * @param BAMFileName name of the BAM file.
     * @throws GenomeFileException if input BAM file is invalid.
     */
    public BAMParser(String BAMFileName) throws GenomeFileException {
        this.BAMFile = new File(BAMFileName);
        if (isInvalid(this.BAMFile)) {
            throw new GenomeFileException(this.getClass().getName(), "BAMParser", BAMFileName, "error occurred during file validation");
        }

    }

    /**
     * Validates the input BAM file.
     *
     * @return true if BAM file is not valid, else return false.
     */
    private static boolean isInvalid(File BAMFile) {
        if (!BAMFile.exists()) {
            return true;
        }

        if (!BAMFile.canRead() || !BAMFile.isFile()) {
            return true;
        }

        String[] filename = BAMFile.getName().split("\\.");
        String extension = filename[filename.length - 1];

        return !extension.toLowerCase().equals(BAM_EXTENSION);
    }

    /**
     * Parse exons from the BAM file.
     *
     * @param exons List of exons that were parsed from the corresponding BED file.
     * @return SAMRecordList of SAMRecords from the current gene
     * @throws GenomeException if anything went wrong
     */
    public SAMRecordList parse(List<BEDFeature> exons) throws GenomeException, GenomeFileException {
        // output SAMRecordList
        SAMRecordList samRecords = new SAMRecordList();
        // pass through all exons
        for (BEDFeature exon : exons) {
            samRecords.addAll(parse(exon));
        }
        return samRecords;
    }

    /**
     * Parse the exon from the input BAM file.
     *
     * @param exon Input exon that should be parsed from the file.
     * @return List with SAM records.
     * @throws GenomeException     if error occurs.
     * @throws GenomeFileException if file error occurs.
     */
    public SAMRecordList parse(BEDFeature exon) throws GenomeException, GenomeFileException {
        List<BEDFeature> smallerExons = generateExons(exon.getStartPos(), exon.getEndPos(), exon.getChromosomeName(), exon.getGene());
        SAMRecordList recordList = new SAMRecordList();
        for (BEDFeature e : smallerExons) {
            recordList.addAll(parseExon(e));
        }
        return recordList;
    }

    /**
     * Parse smaller exons from this BAM file.
     *
     * @param exon exon, which we want to take
     * @return SAMRecordList of SAMRecords from the current gene
     * @throws GenomeException if error occurs while parsing.
     */
    private SAMRecordList parseExon(BEDFeature exon) throws GenomeException {
        try {
            SAMRecordList samRecords = new SAMRecordList();
            // Start iterating from start to end of current chromosome.
            SamReader samReader = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.STRICT).open(BAMFile);
            SAMRecordIterator iter = samReader.query(exon.getChromosomeName(), exon.getStartPos(), exon.getEndPos(), false);
            // while there are sam strings in this region
            while (iter.hasNext()) {
                // Iterate thorough each record and extract fragment size
                SAMRecord samRecord = iter.next();
                if (samRecord.getEnd() != 0) {
                    samRecords.add(samRecord);
                }
            }
            // stop iterator
            iter.close();
            samReader.close();
            return samRecords;
        } catch (NullPointerException | IllegalArgumentException | SAMException | IOException ioex) {
            // If catch an exception then create our GenomeException exception;
            GenomeException ibfex = new GenomeException(this.getClass().getName(), "parse", ioex.getMessage());
            ibfex.initCause(ioex);
            throw ibfex;
        }
    }

    /**
     * Adds an exon to the resulting list. If the length of the input
     * region is more than MAX_REGION_LENGTH, then splits
     * the input region into small parts.
     *
     * @param start Start position of the region.
     * @param end   End position of the region.
     * @param chrom Name of the chromosome.
     * @param gene  Name of the gene.
     * @return List with the regions from the bed file.
     * @throws GenomeFileException if incorrect parameters were passed while creating BEDFeature object.
     */
    private static List<BEDFeature> generateExons(int start, int end, String chrom, String gene) throws GenomeFileException {
        List<BEDFeature> list = new ArrayList<>();
        // split big region into small parts
        int exonLen = end - start;
        if (exonLen <= MAX_REGION_LENGTH) {
            list.add(new BEDFeature(chrom, start, end, gene));
        } else {
            int tempStart = start;
            for (int tempEnd = start; tempEnd <= end; tempEnd++) {
                if (tempEnd - tempStart > MAX_REGION_LENGTH) {
                    list.add(new BEDFeature(chrom, tempStart, tempEnd, gene));
                    tempStart = tempEnd + 1;
                }
            }
            // if the last region size is lesser than MAX_REGION_LENGTH
            if (end - tempStart > 1) {
                list.add(new BEDFeature(chrom, tempStart, end, gene));
            }
        }
        return list;
    }
}
