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

package bam;

import exception.GenomeException;
import exception.GenomeFileException;
import htsjdk.samtools.*;
import util.LinkedSAMRecordList;

import java.io.File;
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
     * Describes the status of the input BAM file.
     */
    private enum BAMFileStatus {
        OK, CAN_NOT_READ {
            @Override
            public String toString() {
                return "Can not read from this BAM file";
            }
        }, DOES_NOT_EXIST {
            @Override
            public String toString() {
                return "The BAM file does not exist";
            }
        }, INCORRECT_EXTENSION {
            @Override
            public String toString() {
                return "Incorrect extension for the BAM file";
            }
        }, UNREADABLE {
            @Override
            public String toString() {
                return "BAM file is unreadable";
            }
        }
    }

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
     * Name of BAM file.
     */
    private String BAMFileName;

    /**
     * Status of input BAM file
     */
    private BAMFileStatus status = BAMFileStatus.OK;

    /**
     * Sam reader used to parse this bam file.
     */
    private SamReader samReader;

    /**
     * Default class constructor from name of the BAM file and ArrayList of exons(class BEDFeature).
     *
     * @param BAMFileName name of the BAM file.
     * @throws GenomeFileException if input BAM file is invalid.
     */
    public BAMParser(String BAMFileName) throws GenomeFileException {
        this.BAMFileName = BAMFileName;
        this.BAMFile = new File(BAMFileName);
        if (isInvalid()) {
            throw new GenomeFileException(this.getClass().getName(), "BAMParser", this.BAMFile.getName(), this.status.toString());
        }
        samReader = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.STRICT).open(BAMFile);
    }

    /**
     * Validates the input BAM file.
     *
     * @return true if BAM file is not valid, else return false.
     */
    private boolean isInvalid() {
        if (!this.BAMFile.exists()) {
            this.status = BAMFileStatus.DOES_NOT_EXIST;
            return true;
        }

        if (!this.BAMFile.canRead() || !this.BAMFile.isFile()) {
            this.status = BAMFileStatus.CAN_NOT_READ;
            return true;
        }

        String[] filename = this.BAMFileName.split("\\.");
        String extension = filename[filename.length - 1];

        if (!extension.toLowerCase().equals(BAM_EXTENSION)) {
            this.status = BAMFileStatus.INCORRECT_EXTENSION;
            return true;
        }

        return false;
    }

    /**
     * Parse exons from the BAM file.
     *
     * @param exons List of exons that were parsed from the corresponding BED file.
     * @return LinkedSAMRecordList of SAMRecords from the current gene
     */
    public synchronized LinkedSAMRecordList parse(List<BEDFeature> exons) throws GenomeFileException, GenomeException {
        // output LinkedSAMRecordList
        LinkedSAMRecordList samRecords = new LinkedSAMRecordList();
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
     * @throws GenomeException if error occurs.
     */
    public synchronized LinkedSAMRecordList parse(BEDFeature exon) throws GenomeFileException, GenomeException {
        List<BEDFeature> smallerExons = generateExons(exon.getStartPos(), exon.getEndPos(), exon.getChromosomeName(), exon.getGene());
        LinkedSAMRecordList recordList = new LinkedSAMRecordList();
        for (BEDFeature e : smallerExons) {
            recordList.addAll(parseExon(e));
        }
        return recordList;
    }

    /**
     * Parse smaller exons from this BAM file.
     *
     * @param exon exon, which we want to take
     * @return LinkedSAMRecordList of SAMRecords from the current gene
     */
    private synchronized LinkedSAMRecordList parseExon(BEDFeature exon) throws GenomeException {
        try {
            LinkedSAMRecordList samRecords = new LinkedSAMRecordList();
            // Start iterating from start to end of current chromosome.
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
            return samRecords;
        } catch (NullPointerException | IllegalArgumentException | SAMException ex) {
            // If catch an exception then create our GenomeException exception;
            GenomeException ibfex = new GenomeException(this.getClass().getName(), "parse", ex.getMessage());
            ibfex.initCause(ex);
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
            if (tempStart - end > 0) {
                list.add(new BEDFeature(chrom, tempStart, end, gene));
            }
        }
        return list;
    }
}
