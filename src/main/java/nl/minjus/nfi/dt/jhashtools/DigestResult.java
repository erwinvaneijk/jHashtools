/*
 */
package nl.minjus.nfi.dt.jhashtools;

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
}
