/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.minjus.nfi.dt.jhashtools;

import java.util.HashMap;
import java.util.Map;
import nl.minjus.nfi.dt.jhashtools.utils.StringOperations;

/**
 *
 * @author kojak
 */
public class DigestsResults {

    private Map<String, Digest> results;

    public DigestsResults() {
        this.results = new HashMap<String, Digest>();
    }

    public DigestsResults(String algorithm, Digest value) {
        this.results = new HashMap<String, Digest>();
        this.results.put(algorithm, value);
    }

    public boolean containsResult(String key) {
        return this.results.containsKey(key);
    }

    public int size() {
        return this.results.size();
    }

    public Digest getDigest(String key) {
        return this.results.get(key);
    }
    
    public String getHexDigest(String digestName) {
        return this.results.get(digestName).toHex();
    }

    public void setDigest(String digestName, Digest value) {
        this.results.put(digestName, value);
    }

    public Digest digest() {
        return results.values().iterator().next();
    }
}
