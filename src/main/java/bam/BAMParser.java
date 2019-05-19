package bam;

import exception.GenomeException;
import exception.GenomeFileException;
import htsjdk.samtools.*;
import util.LinkedSAMRecordList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
            public String toString() {return "BAM file is unreadable";}
        }
    }

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
     * Reader for BAM file
     */
    private SamReader samReader;

    /**
     * Status of input BAM file
     */
    private BAMFileStatus status = BAMFileStatus.OK;

    /**
     * Deafult class constructor from name of the BAM file and ArrayList of exons(class BEDFeature).
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
    public LinkedSAMRecordList parse(List<BEDFeature> exons) throws GenomeException {

        // output LinkedSAMRecordList
        LinkedSAMRecordList samRecords = new LinkedSAMRecordList();
        // pass through all exons
        for (BEDFeature exon : exons) {
            samRecords.addAll(parse(exon));
        }
        return samRecords;
    }

    /**
     * Parse exons from the BAM file.
     *
     * @param exon exon , which we want to take
     * @return LinkedSAMRecordList of SAMRecords from the current gene
     */
    public LinkedSAMRecordList parse(BEDFeature exon) throws GenomeException {
        try {
            SamReader samReader = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.STRICT).open(BAMFile);
            LinkedSAMRecordList samRecords = new LinkedSAMRecordList();
            // Start iterating from start to end of current chromosome.
            SAMRecordIterator iter = samReader.query(exon.getChromosomeName(), exon.getStartPos(), exon.getEndPos(), true);
            // while there are sam strings in this region
            while (iter.hasNext()) {
                // Iterate thorough each record and extract fragment size
                SAMRecord samRecord = iter.next();
                samRecords.add(samRecord);
            }
            // stop iterator
            iter.close();
            return samRecords;
        }catch (NullPointerException | IllegalArgumentException | SAMException ex) {
            // If catch an exception then create our GenomeException exception;
            GenomeException ibfex = new GenomeException(this.getClass().getName(), "parse", ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }

    }
}
