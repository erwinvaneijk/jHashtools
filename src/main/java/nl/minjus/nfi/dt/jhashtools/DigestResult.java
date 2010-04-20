/*
 */
package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.exceptions.AlgorithmNotFoundException;
import java.util.Collection;
import java.util.TreeSet;

/**
 *
 * @author kojak
 */
public class DigestResult extends TreeSet<Digest> {

    public DigestResult() {
    }

    public DigestResult(Digest value) {
        this.add(value);
    }

    public DigestResult(Collection<Digest> coll) {
        for (Digest d : coll) {
            this.add(d);
        }
    }
    public boolean containsResult(String key) {
        for (Digest e : this) {
            if (e.getAlgorithm().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public Digest getDigest(String key) {
        for (Digest e : this) {
            if (e.getAlgorithm().equals(key)) {
                return e;
            }
        }
        throw new AlgorithmNotFoundException();
    }

    public String getHexDigest(String digestName) {
        return this.getDigest(digestName).toHex();
    }

    public void setDigest(Digest value) {
        this.add(value);
    }

    public Digest digest() {
        return this.iterator().next();
    }

    @Override
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof DigestResult)) {
            DigestResult other = (DigestResult) o;
            for (Digest d : this) {
                if (!other.contains(d)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * matches returns true if the digests available on both
     * arguments (this) and (o) are equal. If a digest algorithm on one
     * side is available, that is not available on the other it is not
     * compared.
     *
     * When there is no match in algorithm, matches returns false.
     *
     * @param other
     * @return
     */
    boolean matches(DigestResult o) {
        if ((o != null) && (o instanceof DigestResult)) {
            DigestResult other = (DigestResult) o;
            boolean found = false;
            for (Digest d : other) {
                found = found || this.contains(d);
            }
            return found;
        } else {
            return false;
        }
    }
}
