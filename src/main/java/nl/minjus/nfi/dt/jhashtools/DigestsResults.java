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

    private Map<String, byte[]> results;

    public DigestsResults() {
        this.results = new HashMap<String, byte[]>();
    }

    public DigestsResults(String algorithm, byte[] value) {
        this.results = new HashMap<String, byte[]>();
        this.results.put(algorithm, value);
    }
    public byte[] getDigest(String key) {
        return this.results.get(key);
    }
    
    public String getHexDigest(String digestName) {
        return StringOperations.hexify(this.results.get(digestName));
    }

    public void setDigest(String digestName, byte[] value) {
        this.results.put(digestName, value);
    }
}
