package bam;

import exception.GenomeException;
import exception.GenomeFileException;
import htsjdk.samtools.SAMException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that parses BED file.
 *
 * @author Sergey Hhatov
 * @author Vladislav Marchenko
 */
public class BEDParser {

    /**
     * Describes the status of the input bed file.
     */
    private enum BEDFileError {
        DOES_NOT_EXIST {
            @Override
            public String toString() {
                return "this BED file doesn't exist";
            }
        }, CAN_NOT_READ {
            @Override
            public String toString() {
                return "can not read from this BED file";
            }
        }, INCORRECT_EXTENSION {
            @Override
            public String toString() {
                return "incorrect extension for the BED file";
            }
        }, OK
    }

    /**
     * Default extension of the bed file.
     */
    private static final String BED_EXTENSION = "bed";

    /**
     * Default start word of the browse line in
     * the bed file we are not interested in.
     */
    private static final String COMMENT_LINE = "#";

    /**
     * Input BED file.
     */
    private File bedFile;

    /**
     * Input BED file status.
     */
    private BEDFileError status = BEDFileError.OK;

    /**
     * Default class constructor from BED file.
     *
     * @param BEDFileName filename of the BED file to create object from.
     * @throws GenomeFileException if file is incorrect.
     */
    public BEDParser(String BEDFileName) throws GenomeFileException {
        this.bedFile = new File(BEDFileName);
        if (isInvalid()) {
            throw new GenomeFileException(this.getClass().getName(), "BEDParser", this.bedFile.getName(), this.status.toString());
        }
    }

    /**
     * Validates the input BED file.
     *
     * @return true if BED file is not valid, else return false.
     */
    private boolean isInvalid() {
        if (!this.bedFile.exists()) {
            this.status = BEDFileError.DOES_NOT_EXIST;
            return true;
        }

        if (!this.bedFile.isFile()) {
            this.status = BEDFileError.CAN_NOT_READ;
            return true;
        }

        String[] filename = this.bedFile.getName().split("\\.");
        String extension = filename[filename.length - 1];

        if (!extension.toLowerCase().equals(BED_EXTENSION)) {
            this.status = BEDFileError.INCORRECT_EXTENSION;
            return true;
        }

        return false;
    }

    /**
     * Parse BED file line by line and create output HashMap (see @return)
     *
     * @return HashMap<String, ArrayList < BEDFeature>> where: key - name of gene,
     * value - ArrayList of BEDFeatures which contain this gene
     * @throws GenomeException if any kind of exception occurs in the method.
     */
    public Map<String, List<BEDFeature>> parse() throws GenomeException {
        // parse file line by line
        try (FileReader input = new FileReader(this.bedFile)) {
            // result HashMap of exons
            Map<String, List<BEDFeature>> exons = new HashMap<>();

            BufferedReader reader = new BufferedReader(input);

            // read from file
            String temp;
            while ((temp = reader.readLine()) != null) {
                // if not useful for us line appears
                if (temp.startsWith(COMMENT_LINE)) {
                    continue;
                }

                String[] rows = temp.split("\\s+");

                // check the input row of the table
                if (rows.length != 4) {
                    // must be at 4 elements in the row
                    throw new GenomeFileException("Error occurred during reading from the file [" + this.bedFile.getName() + "]: " + "incorrect number of rows in the table. Expected 4 (chrom, start, end, gene name), got " + rows.length);
                }

                // elements of the bed file record
                String chrom = rows[0], gene = rows[3];
                int start = Integer.parseInt(rows[1]), end = Integer.parseInt(rows[2]);

                if (exons.containsKey(gene)) {
                    // check if the same gene is located in different chromosomes
                    for (BEDFeature bf : exons.get(gene)) {
                        if (!chrom.equals(bf.getChromosomeName())) {
                            throw new GenomeFileException("Error occurred during reading from the file [" + this.bedFile.getName() + "]: " + "incorrect number of rows in the table. Expected 4 (chrom, start, end, gene name), got " + rows.length);
                        }
                    }
                    exons.get(gene).add(new BEDFeature(chrom, start, end, gene));
                } else {
                    List<BEDFeature> buffer = new ArrayList<>();
                    buffer.add(new BEDFeature(chrom, start, end, gene));
                    exons.put(gene, buffer);
                }
            }
            return exons;
        } catch (SAMException | IOException | NumberFormatException ex) {
            // if catch an exception then create our GenomeException exception,
            // set it as it's cause and throw it further
            GenomeException ibfex = new GenomeException(this.getClass().getName(), "parse", ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }
    }
}
