/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 *
 * @author Erwin van Eijk
 */
public class DirHasherResult implements Iterable<Map.Entry<File, DigestResult>> {

    private ConstructionInfo constructionInfo;

    private final TreeMap<File, DigestResult> content;

    /**
     * Constructor.
     */
    public DirHasherResult() {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param ignoreCase if set to true, the case is ignored on the file when comparing two files for equality.
     */
    public DirHasherResult(boolean ignoreCase) {
        this.content = new TreeMap<File, DigestResult>(new FileComparator(ignoreCase));
        this.constructionInfo = new ConstructionInfo();
    }

    /**
     * put <c>value</c> into the store.
     *
     * @param name the filename to store.
     * @param value the value to store for the filename
     */
    public void put(String name, DigestResult value) {
        this.content.put(new File(name), value);
    }

    /**
     * put <c>value</c> into the store.
     *
     * @param file the File to store.
     * @param value the value to store for the file
     */
    public void put(File file, DigestResult value) {
        this.content.put(file, value);
    }

    /**
     * Put all the elements in <c>map</c> into our content.
     *
     * @param map the map to get all the content from.
     */
    public synchronized void putAll(Map<? extends File, ? extends DigestResult> map) {
        this.content.putAll(map);
    }

    /**
     * Put all the elements in <c>other</c> into our content.
     *
     * @param other the other party to get all the results from.
     */
    public synchronized void putAll(DirHasherResult other) {
        this.content.putAll(other.content);
    }

    /**
     * used to check that a file with name exists.
     *
     * @param name the name to check.
     * @return true if it exists.
     */
    public boolean containsKey(String name) {
        return this.content.containsKey(new File(name));
    }

    /**
     * used to check that a file with name exists.
     *
     * @param file the file to check.
     * @return true if it exists.
     */
    public boolean containsKey(File file) {
        return this.content.containsKey(file);
    }

    /**
     * Get the digestresult by name.
     *
     * @param name the name to check for.
     * @return a digestresult or null if it does not exist.
     */
    public DigestResult get(String name) {
        return this.content.get(new File(name));
    }

    /**
     * Get the digestresult by file.
     *
     * @param file the file to check for.
     * @return a digestresult or null if it does not exist.
     */
    public DigestResult get(File file) {
        return this.content.get(file);
    }

    /**
     * Get the results by using only the algorithm mentioned.
     *
     * @param algorithm the algorithm to look for.
     * @return the subset of this instance with only entries which have the correct algorithm defined.
     */
    public DirHasherResult getByAlgorithm(String algorithm) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry: this.content.entrySet()) {
            result.put(entry.getKey(), new DigestResult(entry.getValue().getDigest(algorithm)));
        }
        return result;
    }

    /**
     * Compare this instance with another instance for equality. The content of the ConstructionInfo member
     * is <i>ignored</i>.
     *
     * @param other the instance to compare to.
     * @return true if all elements contained are the same.
     */
    @Override
    public boolean equals(Object other) {
        if (this != other && other instanceof DirHasherResult) {
            DirHasherResult o = (DirHasherResult) other;
            return this.content.equals(o.content);
        } else {
            return false;
        }
    }

    /**
     * Compare this instance with another instance for equality. The content of the ConstructionInfo member
     * is <i>ignored</i>.
     *
     * @param other the instance to compare to.
     * @return true if all elements contained are the same.
     */
    public boolean matches(DirHasherResult other) {
        for (Map.Entry<File, DigestResult> entry: this) {
            if (!(other.containsKey(entry.getKey()) && entry.getValue().matches(other.get(entry.getKey())))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get this structure as a ordinary Map.
     *
     * @return a map. Do not touch!
     */
    public final Map<File, DigestResult> getContent() {
        return this.content;
    }

    /**
     * Get the number of elements available.
     *
     * @return the number of elements.
     */
    public int size() {
        return this.content.size();
    }

    /**
     * Clear the content. Do NOT clear the ConstructionInfo.
     */
    public void clear() {
        this.content.clear();
    }

    /**
     * Get the first entry of our conent.
     *
     * @return a map entry.
     */
    public Map.Entry<File, DigestResult> firstEntry() {
        return this.content.firstEntry();
    }

    /**
     * Exclude all entries that are in o from this set and return
     * the resulting set.
     *
     * @param o the other set to compare <c>this</c> to.
     * @return the set of all entries in this, but not in <c>o</c>.
     */
    public DirHasherResult exclude(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (o.containsKey(key)) {
                if (!value.matches(o.get(key))) {
                    result.put(key, value);
                }
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Like exclude, but only include entries from <c>o</c> that are
     * in this, but are wrong.
     * @param o the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult includeWrong(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (o.containsKey(key) && !value.matches(o.get(key))) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Get the entries that are in both selections.
     *
     * @param o the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult intersect(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            File key = entry.getKey();
            DigestResult digestForKey = entry.getValue();
            if (o.containsKey(key)) {
                DigestResult otherKey = o.get(key);
                if (digestForKey.matches(otherKey)) {
                    result.put(key, digestForKey);
                } else if (otherKey.matches(digestForKey)) {
                    result.put(key, otherKey);
                }
            }
        }
        return result;
    }

    /**
     * Get all the entries that are in a or b but not in both.
     *
     * @param o the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult notIntersect(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (! (o.containsKey(key) && value.matches(o.get(key)))) {
                result.put(key, value);
            }
        }
        for (Map.Entry<File, DigestResult> entry : o.content.entrySet()) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (! (this.containsKey(key) && value.matches(this.get(key)))) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Return the set of results that are in this set, but are missing from
     * the other set.
     * @param other the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult missing(DirHasherResult other) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (! (other.containsKey(key) && value.equals(other.get(key)))) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * @return the constructionInfo
     */
    public ConstructionInfo getConstructionInfo() {
        return constructionInfo;
    }

    /**
     * @param constructionInfo the constructionInfo to set
     */
    public void setConstructionInfo(ConstructionInfo constructionInfo) {
        this.constructionInfo = constructionInfo;
    }

    /**
     * Create nice output to the <c>out</c> PrintStream.
     *
     * @param out where to write to.
     */
    public void prettyPrint(PrintWriter out) {
        out.printf("%s\n", this.content.firstKey());
        DigestResult res = this.content.firstEntry().getValue();
        for (Digest d: res) {
            out.printf("\t%s\n", d.prettyPrint('\t'));
        }
    }

    /**
     * Get an iterator for this beast.
     * @return the iterator
     */
    public Iterator<Entry<File, DigestResult>> iterator() {
        return this.content.entrySet().iterator();
    }

}
