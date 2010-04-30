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
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author eijk
 */
public class DirHasherResult extends TreeMap<File, DigestResult> {

    private ConstructionInfo constructionInfo;

    /**
     * Constructor.
     */
    public DirHasherResult() {
        super(new FileComparator());
        this.constructionInfo = new ConstructionInfo();
    }

    /**
     * put <c>value</c> into the store.
     *
     * @param name the filename to store.
     * @param value the value to store for the filename
     */
    public void put(String name, DigestResult value) {
        this.put(new File(name), value);
    }

    /**
     * used to check that a file with name exists.
     *
     * @param name the name to check.
     * @return true if it exists.
     */
    public boolean containsKey(String name) {
        return this.containsKey(new File(name));
    }

    /**
     * Get the digestresult by name.
     *
     * @param name the name to check for.
     * @return a digestresult or null if it does not exist.
     */
    public DigestResult get(String name) {
        return this.get(new File(name));
    }
    
    /**
     * Get the results by using only the algorithm mentioned.
     * 
     * @param algorithm the algorithm to look for.
     * @return
     */
    public DirHasherResult getByAlgorithm(String algorithm) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry: this.entrySet()) {
            result.put(entry.getKey(), new DigestResult(entry.getValue().getDigest(algorithm)));
        }
        return result;
    }

    /**
     * Exclude all entries that are in o from this set and return
     * the resulting set.
     * 
     * @param o
     * @return the set of all entries in this, but not in <c>o</c>.
     */
    public DirHasherResult exclude(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.entrySet()) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (o.containsKey(key)) {
                if (!value.equals(o.get(key))) {
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
     * @param o
     * @return
     */
    public DirHasherResult includeWrong(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.entrySet()) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (o.containsKey(key) && !value.equals(o.get(key))) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Get the entries that are in both selections.
     *
     * @param o
     * @return
     */
    public DirHasherResult intersect(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.entrySet()) {
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
     * @param o
     * @return
     */
    public DirHasherResult notIntersect(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<File, DigestResult> entry : this.entrySet()) {
            File key = entry.getKey();
            DigestResult value = entry.getValue();
            if (! (o.containsKey(key) && value.equals(o.get(key)))) {
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
    public void prettyPrint(PrintStream out) {
        out.printf("%s\n", this.firstKey());
        DigestResult res = this.firstEntry().getValue();
        for (Digest d: res) {
            out.printf("\t%s\n", d.prettyPrint('\t'));
        }
    }
}
