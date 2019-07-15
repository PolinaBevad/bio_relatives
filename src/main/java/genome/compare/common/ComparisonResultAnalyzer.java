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
package genome.compare.common;

import java.util.Collection;

/**
 * Interface , which defines interface of all the classes, that
 * analyze results of gene comparison
 *
 * @author Vladislav Marchenko
 */
public interface ComparisonResultAnalyzer {
    /**
     * Method for adding a list of gene comparison results for storage and analyzing.
     *
     * @param collection Collection with the results of the comparison.
     */
    default void add(Collection<? extends ComparisonResult> collection) {
        for (ComparisonResult element : collection) {
            this.add(element);
        }
    }

    /**
     * Method for adding one gene comparison result for storage and analyzing.
     *
     * @param comparisonResult one of the gene comparison result
     */
    void add(ComparisonResult comparisonResult);

    /**
     * Method for analyzing of comparison results.
     *
     * @return String representation of the result analysis.
     */
    String analyze();
}
