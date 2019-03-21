package bam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import htsjdk.samtools.*;

import exception.InvalidBAMFileException;


/**
 * Class for parsing of BAM files to ArrayList of SUMRecords.
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
     * Output ArrayList of SAMRecords.
     */
    private ArrayList<SAMRecord> samRecords = new ArrayList<>();

    /**
     * Output ArrayList<String> of qualities of each output SAMRecord.
     */
    private ArrayList<String> qualities = new ArrayList<>();

    /**
     * Status of input BAM file
     */
    private BAMFileStatus status = BAMFileStatus.OK;

    /**
     * Deafult class constructor from BAM file and ArrayList of exons(class BEDFeature).
     * @param BAMFile BAM file.
     * @param exons ArrayList of exons from the BED file.
     * @throws InvalidBAMFileException if input BAM file is invalid.
     */
    public BAMParser(File BAMFile, ArrayList<BEDParser.BEDFeature> exons) throws InvalidBAMFileException {
        this.BAMFile = BAMFile;
        if (!isValid()) {
            throw new InvalidBAMFileException (
                    "Error occured during validation of file [" + this.BAMFile.getName() + "] " + this.status
            );
        }
        this.BAMFileName = BAMFile.getName();
        this.exons =  exons;
    }

    /**
     * Deafult class constructor from name of the BAM file and ArrayList of exons(class BEDFeature).
     * @param BAMFileName name of the BAM file.
     * @param exons ArrayList of exons from the BED file.
     * @throws InvalidBAMFileException if input BAM file is invalid.
     */
    public BAMParser(String BAMFileName, ArrayList<BEDParser.BEDFeature> exons) throws InvalidBAMFileException  {
        this.BAMFileName = BAMFileName;
        this.BAMFile = new File(BAMFileName);
        if (!isValid()) {
            throw new InvalidBAMFileException (
                    "Error occured during validation of file [" + this.BAMFileName + "] " + this.status
            );
        }
        this.exons = exons;
    }

    /**
     * Validates the input BAM file.
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
     * Parse BAM/SAM file on exons.
     * Takes an ArrayList of SAMRecords and ArrayList of qualities of each SAMREcord( ArrayList of String).
     */
    public void parse() throws InvalidBAMFileException {
        // Opening of the BAMFile
        SamReader samReader= SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).
                open(BAMFile);
        try {
            // pass through all exons
            for (int i = 0; i < exons.size(); i++) {
                // Start iterating from start to end of current chromosome.
                SAMRecordIterator iter = samReader.query(exons.get(i).getChromosomeName(), exons.get(i).getStartPos(), exons.get(i).getEndPos(), true);


                while(iter.hasNext()){
                    // Iterate thorough each record and extract fragment size
                    SAMRecord rec = iter.next();
                    samRecords.add(rec);
                    qualities.add(rec.getBaseQualityString());
                }
                // stop iterator
                iter.close();

            }

        }
        catch (NullPointerException | IllegalArgumentException  ex) {
            // If catch an exception then create our InvalidBEDFileException exception;
            InvalidBAMFileException ibfex = new InvalidBAMFileException(ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }

    }

    /**
     * Get the list of SAM records method.
     * @return ArrayList of SAMRecords
     */
    public ArrayList<SAMRecord> getSamRecords() {
        return samRecords;
    }

    /**
     * Get the qualities of each region method.
     * @return ArrayList of String with qualities of each region of genome(SAMRecord)
     */
    public ArrayList<String> getQualities() {
        return qualities;
    }

}
