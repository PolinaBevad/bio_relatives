package bam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import exception.InvalidBEDFeatureException;
import exception.InvalidBEDFileException;
import htsjdk.samtools.SAMException;
import htsjdk.samtools.SAMFormatException;

/**
 * Class that parses BED file.
 *
 * @author Sergey Hhatov
 */
public class BEDParser
{
    /**
     *
     */
    public static class BEDFeature
    {
        /**
         * Set of characters that are allowed in genome symbol name.
         */
        private static HashSet<Character> ALLOWED_SYMBOLS = new HashSet<>();
        static
        {
            for (char ch = 'a'; ch <= 'z'; ch++)
            {
                ALLOWED_SYMBOLS.add(ch);
            }
        }

        /**
         * Set of numbers that are allowed in genome symbol name.
         */
        private static HashSet<Character> ALLOWED_NUMBERS = new HashSet<>();
        static
        {
            for (char ch = '0'; ch <= '9'; ch++)
            {
                ALLOWED_NUMBERS.add(ch);
            }
        }

        /**
         * Name of the chromosome.
         */
        private String chrom_;

        /**
         * Start position of the feature.
         */
        private int start_;

        /**
         * End position of the feature.
         */
        private int end_;

        /**
         * Name of the genome sequence.
         */
        private String genomeSymbol_;

        /**
         * Default class constructor from chromosome name, start and positions of the feature.
         *
         * @param chrom        Name of the chromosome.
         * @param start        Start position.
         * @param end          End position.
         * @param genomeSymbol Name of the genome sequence.
         * @throws InvalidBEDFeatureException if start or end positions are incorrect.
         */
        public BEDFeature(String chrom, int start, int end, String genomeSymbol) throws InvalidBEDFeatureException
        {
            if (chrom == null)
            {
                throw new IllegalArgumentException("Error occurred while creating BEDFeature object: [chrom] argument is null.");
            }
            this.chrom_ = chrom;

            if (genomeSymbol == null)
            {
                throw new IllegalArgumentException("Error occurred while creating BEDFeature object: [genomeSymbol] argument is null.");
            }
            boolean containsChars = false;
            for (char ch : genomeSymbol.toLowerCase().toCharArray())
            {
                // check if input string contains inappropriate symbols
                if (!ALLOWED_SYMBOLS.contains(ch) && !ALLOWED_NUMBERS.contains(ch))
                {
                     throw new InvalidBEDFeatureException("Error occurred during initialization of BEDFeature object: " +
                    "Incorrect parameter was passed: [" + genomeSymbol + "]");
                }
                if (ALLOWED_SYMBOLS.contains(ch))
                {
                    containsChars = true;
                }
            }
            if (!containsChars)
            {
                throw new InvalidBEDFeatureException("Error occurred during initialization of BEDFeature object: " +
                    "Incorrect parameter was passed: [" + genomeSymbol + "]");
            }
            this.genomeSymbol_ = genomeSymbol;

            if (start <= 0 || end <= 0 || start >= end)
            {
                throw new InvalidBEDFeatureException("Error occurred during initialization of BEDFeature object: " +
                    "Incorrect parameters were passed: [" + chrom + ", " + start + ", " + end + "]");
            }

            this.start_ = start;
            this.end_ = end;
        }

        /**
         * Get the nam of the chromosome method.
         *
         * @return Name of the chromosome.
         */
        public String getChromosomeName()
        {
            return chrom_;
        }

        /**
         * Get the start position method.
         *
         * @return Start position of the feature in the chromosome.
         */
        public int getStartPos()
        {
            return start_;
        }

        /**
         * Get the end position method.
         *
         * @return End position of the feature in the chromosome.
         */
        public int getEndPos()
        {
            return end_;
        }

        /**
         * Get the genome symbol filed value method.
         *
         * @return Genome symbol filed value.
         */
        public String getGenomeSymbol()
        {
            return genomeSymbol_;
        }
    }

    /**
     * Describes the status of the input bed file.
     */
    private enum BEDFileError
    {
        DOES_NOT_EXIST
            {
                @Override
                public String toString()
                {
                    return "this BED file doesn't exist";
                }
            },
        CAN_NOT_READ
            {
                @Override
                public String toString()
                {
                    return "can not read from this BED file";
                }
            },
        INCORRECT_EXTENSION
            {
                @Override
                public String toString()
                {
                    return "incorrect extension for the BED file";
                }
            },
        OK
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
     * @throws InvalidBEDFileException  if file is incorrect.
     * @throws IllegalArgumentException if parameter is null.
     */
    public BEDParser(String BEDFileName) throws InvalidBEDFileException, IllegalArgumentException
    {
        if (BEDFileName == null)
        {
            throw new IllegalArgumentException("Error occurred while creating BEDParser object: [BEDFileName] argument is null.");
        }
        this.bedFile = new File(BEDFileName);
        if (validate())
        {
            throw new InvalidBEDFileException("Error occurred during validation of the file [" + this.bedFile.getName() + "]: " + this.status);
        }
    }

    /**
     * Validates the input BED file.
     *
     * @return true if BED file is not valid, else return false.
     */
    private boolean validate()
    {
        if (!this.bedFile.exists())
        {
            this.status = BEDFileError.DOES_NOT_EXIST;
            return true;
        }

        if (!this.bedFile.isFile())
        {
            this.status = BEDFileError.CAN_NOT_READ;
            return true;
        }

        String[] filename = this.bedFile.getName().split("\\.");
        String extension = filename[filename.length - 1];

        if (!extension.toLowerCase().equals(BED_EXTENSION))
        {
            this.status = BEDFileError.INCORRECT_EXTENSION;
            return true;
        }

        return false;
    }

    /**
     * Parse BED file line by line and create list of {@link BEDFeature} objects.
     *
     * @return List of parsed from the BED file {@link BEDFeature} objects.
     */
    public ArrayList<BEDFeature> parse() throws InvalidBEDFileException
    {
        // parse file line by line
        try (FileReader input = new FileReader(this.bedFile))
        {
            // result list of exons
            ArrayList<BEDFeature> exons = new ArrayList<>();

            BufferedReader reader = new BufferedReader(input);

            // read from file
            String temp;
            while ((temp = reader.readLine()) != null)
            {
                // if not useful for us line appears
                if (temp.startsWith(COMMENT_LINE))
                {
                    continue;
                }

                String[] rows = temp.split("\\s+");

                // check the input row of the table
                if (rows.length != 4)
                {
                    // must be at 4 elements in the row
                    throw new InvalidBEDFileException("Error occurred during reading from the file [" + this.bedFile.getName() + "]: " +
                        "incorrect number of rows in the table. Expected 4 (chrom, start, end, genome name), got " + rows.length);
                }
                exons.add(new BEDFeature(rows[0], Integer.parseInt(rows[1]), Integer.parseInt(rows[2]), rows[3]));
            }
            return exons;
        } catch (SAMException | IOException | NumberFormatException | InvalidBEDFeatureException ex)
        {
            // if catch an exception then create our InvalidBEDFileException exception,
            // set IOException as it's cause and throw it further
            InvalidBEDFileException ibfex = new InvalidBEDFileException(ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }
    }
}
