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

package bam.marker_region;

import bam.regular.BEDFeature;
import bam.regular.BEDParser;
import exception.GenomeException;
import exception.GenomeFileException;
import htsjdk.samtools.SAMException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class that parses marker region file.
 *
 * @author Sergey Hhatov
 * @author Vladislav Marchenko
 */
public class MarkerRegionFileParser extends BEDParser {

    /**
     * Default class constructor from path to the marker region file.
     *
     * @param markerRegionFileName filename of the BED file to create object from.
     * @throws GenomeFileException if file is incorrect.
     */
    public MarkerRegionFileParser(String markerRegionFileName)  {
        super(markerRegionFileName);
    }

    /**
     * Parse marker region file line by line and create output HashMap (see @return)
     *
     * @return HashMap<String, ArrayList <BEDFeature>> where: key - name of gene,
     * value - ArrayList of BEDFeatures which contain this gene
     * @throws GenomeFileException if any kind of exception occurs in the method.
     */
    @Override
    public Map<String, List<BEDFeature>> parse() {
        // parse file line by line
        try (FileReader input = new FileReader(this.bedFile)) {
            // result HashMap of exons
            Map<String, List<BEDFeature>> exons = new HashMap<>();
            // map with all the pre-compiled motif strings
            Map<String, Pattern> compiledPatterns = new HashMap<>();
            // open reader
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
                if (rows.length != 5) {
                    if (temp.equals("\n")) {
                        continue;
                    }
                    // must be at 4 elements in the row
                    throw new GenomeFileException("Error occurred during reading from the file [" + this.bedFile.getName() + "]: " + "incorrect number of rows in the table. Expected 5 (chrom, start, end, marker name, marker motif), got " + rows.length);
                }

                // elements of the bed file record
                String chrom = rows[0], markerName = rows[3], markerMotif = rows[4];
                int start = Integer.parseInt(rows[1]), end = Integer.parseInt(rows[2]);
                if (exons.containsKey(markerName)) {
                    if (!chrom.contains("Y") && !chrom.contains("X")) {
                        throw new GenomeFileException("Error occurred during reading from the file [" + this.bedFile.getName() + "]: " + "incorrect chromosome name, expected X or Y, found: " + chrom);
                    }
                    exons.get(markerName).add(new MarkerRegionFeature(chrom, start, end, markerName, compiledPatterns.get(markerName)));
                } else {
                    List<BEDFeature> buffer = new ArrayList<>();
                    compiledPatterns.put(markerName, Pattern.compile(markerMotif));
                    buffer.add(new MarkerRegionFeature(chrom, start, end, markerName, compiledPatterns.get(markerName)));
                    exons.put(markerName, buffer);
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
