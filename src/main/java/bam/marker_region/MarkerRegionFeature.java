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
import exception.GenomeFileException;

import java.util.regex.Pattern;

/**
 * marker region file record class.
 *
 * @author Sergey Khvatov
 */
public class MarkerRegionFeature extends BEDFeature {

    /**
     * Default value for the 'gene', which contains this marker region
     * when we are in the X_STR mode.
     */
    private static final String X_STR = "X_STR";

    /**
     * Default value for the 'gene', which contains this marker region
     * when we are in the Y_STR mode.
     */
    private static final String Y_STR = "Y_STR";

    /**
     * Name of the marker.
     */
    private String markerName;

    /**
     * Repeat motif of the STR.
     */
    private Pattern repeatMotif;

    /**
     * Creates an instance of the marker region record.
     *
     * @param chrom      Name of the chromosome.
     * @param start      Start position of the region.
     * @param end        End position.
     * @param markerName Name of the marker.
     * @param motif      Nucleotide sequence which represents this marker.
     * @throws GenomeFileException if invalid input occurs.
     */
    public MarkerRegionFeature(String chrom, int start, int end, String markerName, Pattern motif) throws GenomeFileException {
        super(chrom, start, end, chrom.contains("Y") ? Y_STR : X_STR);
        this.markerName = markerName;
        this.repeatMotif = motif;
    }

    /**
     * @return name of the marker.
     */
    public String getMarkerName() {
        return markerName;
    }

    /**
     * @return repeat motif value.
     */
    public Pattern getRepeatMotif() {
        return repeatMotif;
    }
}
