package bam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import htsjdk.samtools.*;

import exception.InvalidBAMFileException;


/**
 * Class for parsing of BAM files to ArrayList of SAMRecords.
 *
 * @author Vladislav Marchenko
 */
public class BAMParser {
    /**
     * Describes the status of the input BAM file.
     */
    private enum BAMFileStatus {
        OK,
        CAN_NOT_READ {
            @Override
            public String toString() {
                return "Can not read from this BAM file";
            }
        },
        DOES_NOT_EXIST {
            @Override
            public String toString() {
                return "The BAM file does not exist";
            }
        },
        INCORRECT_EXTENSION {
            @Override
            public String toString() {
                return "Incorrect extension for the BAM file";
            }
        }
    }

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
     * ArrayList of exons (BEDFeatures) for which BAM file will be parsed.
     */
    private ArrayList<BEDParser.BEDFeature> exons;

    /**
     * Status of input BAM file
     */
    private BAMFileStatus status = BAMFileStatus.OK;

    /**
     * Default class constructor from BAM file and ArrayList of exons(class BEDFeature).
     *
     * @param BAMFile BAM file.
     * @param exons   ArrayList of exons from the BED file.
     * @throws InvalidBAMFileException if input BAM file is invalid.
     */
    public BAMParser(File BAMFile, ArrayList<BEDParser.BEDFeature> exons) throws InvalidBAMFileException {
        this.BAMFile = BAMFile;
        if (!isValid()) {
            throw new InvalidBAMFileException("Error occured during validation of file [" + this.BAMFile.getName() + "] " + this.status);
        }
        this.BAMFileName = BAMFile.getName();
        this.exons = exons;
    }

    /**
     * Deafult class constructor from name of the BAM file and ArrayList of exons(class BEDFeature).
     *
     * @param BAMFileName name of the BAM file.
     * @param exons       ArrayList of exons from the BED file.
     * @throws InvalidBAMFileException if input BAM file is invalid.
     */
    public BAMParser(String BAMFileName, ArrayList<BEDParser.BEDFeature> exons) throws InvalidBAMFileException {
        this.BAMFileName = BAMFileName;
        this.BAMFile = new File(BAMFileName);
        if (!isValid()) {
            throw new InvalidBAMFileException("Error occured during validation of file [" + this.BAMFileName + "] " + this.status);
        }
        this.exons = exons;
    }

    /**
     * Validates the input BAM file.
     *
     * @return false if BAM file is not valid, else return true.
     */
    private boolean isValid() {
        if (!this.BAMFile.exists()) {
            this.status = BAMFileStatus.DOES_NOT_EXIST;
            return false;
        }

        if (!this.BAMFile.canRead() || !this.BAMFile.isFile()) {
            this.status = BAMFileStatus.CAN_NOT_READ;
            return false;
        }

        String[] filename = this.BAMFileName.split("\\.");
        String extension = filename[filename.length - 1];

        if (!extension.toLowerCase().equals(BAM_EXTENSION)) {
            this.status = BAMFileStatus.INCORRECT_EXTENSION;
            return false;
        }

        return true;
    }


    /**
     * Parse BAM file on exons.
     *
     * @return ArrayList of SAMRecords
     */
    public ArrayList<SAMRecord> parse() throws InvalidBAMFileException {

        try {
            // Opening of the BAMFile
            SamReader samReader = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.STRICT).open(BAMFile);
            // ArrayLIst of Samrecords , which we will return
            ArrayList<SAMRecord> samRecords = new ArrayList<>();
            // pass through all exons
            for (int i = 0; i < exons.size(); i++) {
                // Start iterating from start to end of current chromosome.
                SAMRecordIterator iter = samReader.query(exons.get(i).getChromosomeName(), exons.get(i).getStartPos(), exons.get(i).getEndPos(), true);

                // while there are sam strings in this region
                while (iter.hasNext()) {
                    // Iterate thorough each record and extract fragment size
                    SAMRecord rec = iter.next();
                    samRecords.add(rec);
                }
                // stop iterator
                iter.close();

            }
            return samRecords;
        } catch (NullPointerException | IllegalArgumentException | SAMException ex) {
            // If catch an exception then create our InvalidBEDFileException exception;
            InvalidBAMFileException ibfex = new InvalidBAMFileException(ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }

    }
}
