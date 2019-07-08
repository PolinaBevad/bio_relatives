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

package bam.regular;

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
     * Default extension of the bed file.
     */
    private static final String BED_EXTENSION = "bed";

    /**
     * Default start word of the browse line in
     * the bed file we are not interested in.
     */
    protected static final String COMMENT_LINE = "#";

    /**
     * Input BED file.
     */
    protected File bedFile;

    /**
     * Default class constructor from BED file.
     *
     * @param BEDFileName filename of the BED file to create object from.
     * @throws GenomeFileException if file is incorrect.
     */
    public BEDParser(String BEDFileName) {
        this.bedFile = new File(BEDFileName);
        if (isInvalid()) {
            throw new GenomeFileException(this.getClass().getName(), "BEDParser", this.bedFile.getName(), "error occurred during file validation");
        }
    }

    /**
     * Validates the input BED file.
     *
     * @return true if BED file is not valid, else return false.
     */
    private boolean isInvalid() {
        if (!this.bedFile.exists()) {
            return true;
        }

        if (!this.bedFile.isFile()) {
            return true;
        }

        String[] filename = this.bedFile.getName().split("\\.");
        String extension = filename[filename.length - 1];

        return !extension.toLowerCase().equals(BED_EXTENSION);
    }

    /**
     * Parse BED file line by line and create output HashMap (see @return)
     *
     * @return HashMap<String, ArrayList <BEDFeature>> where: key - name of gene,
     * value - ArrayList of BEDFeatures which contain this gene
     * @throws GenomeFileException if any kind of exception occurs in the method.
     */
    public Map<String, List<BEDFeature>> parse()  {
        // parse file line by line
        try (FileReader input = new FileReader(this.bedFile)) {
            // result HashMap of exons
            Map<String, List<BEDFeature>> exons = new HashMap<>();

            BufferedReader reader = new BufferedReader(input);
            String temp;
            // read from file
            while ((temp = reader.readLine()) != null) {
                // if not useful for us line appears
                if (temp.startsWith(COMMENT_LINE)) {
                    continue;
                }

                String[] rows = temp.split("\\s+");
                // check the input row of the table
                if (rows.length != 4) {
                    if (temp.equals("\n")) {
                        continue;
                    }
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
                            throw new GenomeFileException("Error! The same gene " + bf.getChromosomeName() + " is found in different chromosomes.");
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
            GenomeFileException ibfex = new GenomeFileException(this.getClass().getName(), "parse", this.bedFile.getName(), ex.getMessage());
            ibfex.initCause(ex);
            throw ibfex;
        }
    }
}
