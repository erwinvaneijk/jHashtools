/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author eijk
 */
public class DirHasherResult extends TreeMap<String, DigestResult> {


    public DirHasherResult getByAlgorithm(String algorithm) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<String, DigestResult> entry: this.entrySet()) {
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
        for (Map.Entry<String, DigestResult> entry : this.entrySet()) {
            String key = entry.getKey();
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
        for (Map.Entry<String, DigestResult> entry : this.entrySet()) {
            String key = entry.getKey();
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
        for (Map.Entry<String, DigestResult> entry : this.entrySet()) {
            String key = entry.getKey();
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
     * @param digests
     * @return
     */
    public DirHasherResult notIntersect(DirHasherResult o) {
        DirHasherResult result = new DirHasherResult();
        for (Map.Entry<String, DigestResult> entry : this.entrySet()) {
            String key = entry.getKey();
            DigestResult value = entry.getValue();
            if (! (o.containsKey(key) && value.equals(o.get(key)))) {
                result.put(key, value);
            }
        }
        return result;
    }
}
