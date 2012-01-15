/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>. All rights reserved.
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
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of <copyright holder>.
 */

package nl.minjus.nfi.dt.jhashtools;

import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.minjus.nfi.dt.jhashtools.exceptions.NoMatchingAlgorithmsError;

/**
 * An abstraction for a set of map entries.
 *
 * @author Erwin van Eijk
 */
public class DirHasherResult implements Iterable<Map.Entry<File, DigestResult>>
{
    private static final Logger LOG = getLogger(DigestOutputCreator.class.getCanonicalName());

    private ConstructionInfo constructionInfo;

    private final TreeMap<File, DigestResult> content;

    /**
     * Constructor.
     */
    public DirHasherResult()
    {
        this(false);
    }

    /**
     * Constructor.
     *
     * @param ignoreCase
     *            if set to true, the case is ignored on the file when comparing two files for equality.
     */
    public DirHasherResult(final boolean ignoreCase)
    {
        this.content = new TreeMap<File, DigestResult>(new FileComparator(ignoreCase));
        this.constructionInfo = new ConstructionInfo();
    }

    /**
     * put <c>value<c> into the store.
     *
     * @param name
     *            the filename to store.
     * @param value
     *            the value to store for the filename
     */
    public void put(final String name, final DigestResult value) {
        this.content.put(new File(name), value);
    }

    /**
     * put <c>value<c> into the store.
     *
     * @param file
     *            the File to store.
     * @param value
     *            the value to store for the file
     */
    public void put(final File file, final DigestResult value) {
        this.content.put(file, value);
    }

    /**
     * Put all the elements in <c>map<c> into our content.
     *
     * @param map
     *            the map to get all the content from.
     */
    public synchronized void putAll(final Map<? extends File, ? extends DigestResult> map) {
        this.content.putAll(map);
    }

    /**
     * Put all the elements in <c>other<c> into our content.
     *
     * @param other
     *            the other party to get all the results from.
     */
    public synchronized void putAll(final DirHasherResult other) {
        this.content.putAll(other.content);
    }

    /**
     * used to check that a file with name exists.
     *
     * @param name
     *            the name to check.
     * @return true if it exists.
     */
    public boolean containsKey(final String name) {
        return this.content.containsKey(new File(name));
    }

    /**
     * used to check that a file with name exists.
     *
     * @param file
     *            the file to check.
     * @return true if it exists.
     */
    public boolean containsKey(final File file) {
        return this.content.containsKey(file);
    }

    /**
     * Get the digestresult by name.
     *
     * @param name
     *            the name to check for.
     * @return a digestresult or null if it does not exist.
     */
    public DigestResult get(final String name) {
        return this.content.get(new File(name));
    }

    /**
     * Get the digestresult by file.
     *
     * @param file
     *            the file to check for.
     * @return a digestresult or null if it does not exist.
     */
    public DigestResult get(final File file) {
        return this.content.get(file);
    }

    /**
     * Get the results by using only the algorithm mentioned.
     *
     * @param algorithm
     *            the algorithm to look for.
     * @return the subset of this instance with only entries which have the correct algorithm defined.
     */
    public DirHasherResult getByAlgorithm(final String algorithm) {
        final DirHasherResult result = new DirHasherResult();
        for (final Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            result.put(entry.getKey(), new DigestResult(entry.getValue().getDigest(algorithm)));
        }
        return result;
    }

    /**
     * Compare this instance with another instance for equality. The content of the ConstructionInfo member is
     * <i>ignored</i>.
     *
     * @param other
     *            the instance to compare to.
     * @return true if all elements contained are the same.
     */
    @Override
    public boolean equals(final Object other) {
        if (this != other && other instanceof DirHasherResult) {
            final DirHasherResult o = (DirHasherResult) other;
            return this.content.equals(o.content);
        } else {
            return false;
        }
    }

    /**
     * Compute the hashcode.
     *
     * @return an int.
     */
    @Override
    public int hashCode() {
        return this.constructionInfo.hashCode() + this.content.hashCode();
    }

    /**
     * Compare this instance with another instance for equality. The content of the ConstructionInfo member is
     * <i>ignored</i>.
     *
     * @param other
     *            the instance to compare to.
     * @return true if all elements contained are the same.
     */
    public boolean matches(final DirHasherResult other) {
        for (final Map.Entry<File, DigestResult> entry : this) {
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
     * Exclude all entries that are in o from this set and return the resulting set.
     *
     * @param o
     *            the other set to compare <c>this<c> to.
     * @return the set of all entries in this, but not in <c>o<c>.
     */
    public DirHasherResult exclude(final DirHasherResult o) {
        final DirHasherResult result = new DirHasherResult();
        for (final Map.Entry<File, DigestResult> entry : this) {
            final File key = entry.getKey();
            final DigestResult value = entry.getValue();
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
     * Like exclude, but only include entries from <c>o<c> that are in this, but are wrong.
     *
     * @param o
     *            the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult includeWrong(final DirHasherResult o) {
        final DirHasherResult result = new DirHasherResult();
        for (final Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            final File key = entry.getKey();
            final DigestResult value = entry.getValue();
            if (o.containsKey(key) && !value.matches(o.get(key))) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Get the entries that are in both selections.
     *
     * @param o
     *            the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult intersect(final DirHasherResult o) {
        final DirHasherResult result = new DirHasherResult();
        for (final Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            final File key = entry.getKey();
            final DigestResult digestForKey = entry.getValue();
            if (o.containsKey(key)) {
                final DigestResult otherKey = o.get(key);
                try {
                    if (digestForKey.matches(otherKey)) {
                        result.put(key, digestForKey);
                    } else if (otherKey.matches(digestForKey)) {
                        result.put(key, otherKey);
                    }
                } catch (final NoMatchingAlgorithmsError ex) {
                    LOG.log(Level.SEVERE, "Algorithm not supported: " + otherKey.toString());
                }
            }
        }
        return result;
    }

    /**
     * Get all the entries that are in a or b but not in both.
     *
     * @param o
     *            the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult notIntersect(final DirHasherResult o) {
        final DirHasherResult result = new DirHasherResult();
        for (final Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            final File key = entry.getKey();
            final DigestResult value = entry.getValue();
            if (!(o.containsKey(key) && value.matches(o.get(key)))) {
                result.put(key, value);
            }
        }
        for (final Map.Entry<File, DigestResult> entry : o.content.entrySet()) {
            final File key = entry.getKey();
            final DigestResult value = entry.getValue();
            if (!(this.containsKey(key) && value.matches(this.get(key)))) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Return the set of results that are in this set, but are missing from the other set.
     *
     * @param other
     *            the other result to compare this to.
     * @return the correct subset.
     */
    public DirHasherResult missing(final DirHasherResult other) {
        final DirHasherResult result = new DirHasherResult();
        for (final Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            final File key = entry.getKey();
            final DigestResult value = entry.getValue();
            if (!(other.containsKey(key) && value.equals(other.get(key)))) {
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
     * @param constructionInfo
     *            the constructionInfo to set
     */
    public void setConstructionInfo(final ConstructionInfo constructionInfo) {
        this.constructionInfo = constructionInfo;
    }

    /**
     * Create nice output to the <c>out<c> PrintStream.
     *
     * @param out
     *            where to write to.
     */
    public void prettyPrint(final PrintWriter out) {
        out.printf("%d elements\n", this.content.size());
        for (final Map.Entry<File, DigestResult> entry : this) {
            out.printf("\t%s\n", entry.getKey());
            final DigestResult digestResult = entry.getValue();
            for (final Digest d : digestResult) {
                out.printf("\t\t%s\n", d.prettyPrint('\t'));
            }
        }
        out.flush();
    }

    /**
     * Get an iterator for this beast.
     *
     * @return the iterator
     */
    @Override
    public Iterator<Entry<File, DigestResult>> iterator() {
        return this.content.entrySet().iterator();
    }

    /**
     * Get the number of entries in the set that have the same entries.
     *
     * @param digest
     * @return
     */
    public int count(final DigestResult digest) {
        int c = 0;
        for (final Map.Entry<File, DigestResult> entry : this.content.entrySet()) {
            if (entry.getValue().matches(digest)) {
                c += 1;
            }
        }
        return c;
    }
}
