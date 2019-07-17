/*
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

package util;

/**
 * Structure for storage 2 objects
 *
 * @param <K></K> key
 * @param <V></V> value
 * @author Vladislav Marchenko
 */
public class Pair<K, V> {
    /**
     * Key of this Pair
     */
    private K key;
    /**
     * Value of this Pair
     */
    private V value;

    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key for this pair.
     *
     * @return key for this pair
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets the value for this pair.
     *
     * @return value for this pair
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets the value for this pair.
     * @param value for this pair
     */
    public void setValue(V value) {
        this.value = value;
    }


    /**
     * Sets the key for this pair.
     * @param key for this pair
     */
    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "[" + "first=" + key + ", second=" + value + "]";
    }
}
