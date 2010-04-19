/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.utils;

import java.util.HashMap;
import java.util.Map;
import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 *
 * @author kojak
 */
public class KnownDigests {

    public final static DirHasherResult getKnownResults() {
        DirHasherResult knownResults = new DirHasherResult();

        Map<Integer, Digest> expectedSha256Digests = new HashMap<Integer, Digest>();

        DigestResult dr = new DigestResult();
        dr.add(new Digest("sha-256", "589f2fc47ebe5744c2c349a8529dde37218eac8782e4b54bbbabd46bcbdd62ae"));
        dr.add(new Digest("sha-1", "f31ce0511a64b48dd2ba82f2ef5ae3186c4c9440"));
        dr.add(new Digest("md5", "f2b0b325ae68cb310133b89f7122773e"));
        knownResults.put("testdata/testfile1.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "615e3b6489843b9f7fb1a3261918a2b3034a5950972bf09a568b6857aa173154"));
        dr.add(new Digest("sha-1", "39c8bb6db73ddcbe755e25c276e81904cadeabfc"));
        dr.add(new Digest("md5", "0fbbdcca1918105f54d76aa84b3a1d40"));
        knownResults.put("testdata/testfile2.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "d0bdb2f79e6c1298c16ce49aa43686d8e9a2364dc3155a0fc3db7e62cbb75069"));
        dr.add(new Digest("sha-1", "4ac5981591e448efac073c4232a805022b3ed295"));
        dr.add(new Digest("md5", "9164236d8f8140b1724d683d85419893"));
        knownResults.put("testdata/testfile3.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "b39239ce8959672b30a39d2f9ce95e4cfb8c4c531891132d7cdcd8c8684ddf8b"));
        dr.add(new Digest("sha-1", "7b96266c96b18a44dec6f52695aeb8957a8ece86"));
        dr.add(new Digest("md5", "a760e2c39a00c1e92dc8dc9f9882b28b"));
        knownResults.put("testdata/testfile4.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "5ebe81f8113d4bfeb65ef61606018b77e3c1a682243bf94c34e33e42a48e06c4"));
        dr.add(new Digest("sha-1", "a3ff2d0dee09e8481d48448e5889305ee34a1789"));
        dr.add(new Digest("md5", "2b355ce36890b19368a5d231010fd925"));
        knownResults.put("testdata/testfile5.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "bdcba8cb46bf497dfc89f4e4d94e20b567526a1d64497459ef5224543b878905"));
        dr.add(new Digest("sha-1", "a374595ab7cf3ba691e3e4389549a5aad49f7a46"));
        dr.add(new Digest("md5", "19b5212dfb34916a6ab1870a8996a2ac"));
        knownResults.put("testdata/testfile6.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "1f6ee38218bc6fce9f388f75c0900fc68a3e8aacede26117914e765e9cd08d4e"));
        dr.add(new Digest("sha-1", "b7807b4958df45e4cb7ea2aa435d6029096c8094"));
        dr.add(new Digest("md5", "f773b3278d0d7b16577b9298dc69e47c"));
        knownResults.put("testdata/testfile7.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "cf88ce7c5bc7e53e980b1eabe633792913fadbea292d665bc56398ccca5dbc85"));
        dr.add(new Digest("sha-1", "1187ba416ae93ef3be40490171e6f76c4bf933ca"));
        dr.add(new Digest("md5", "305fb003f3cd70921a45031a5e6ea293"));
        knownResults.put("testdata/testfile8.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "f2bb1df1fac49c804e9cfe5d56cbc9c3865b045656d7f68237efa9464683206f"));
        dr.add(new Digest("sha-1", "7c8b4cbd546ec4a3b0314d67d0981a7ea907c356"));
        dr.add(new Digest("md5", "86d62da24929b4251060daaa0ccb8a50"));
        knownResults.put("testdata/testfile9.bin", dr);

        dr = new DigestResult();
        dr.add(new Digest("sha-256", "a4d68e8ee6d7c8a11913bf9703f29ff9082b96eace52cddab1c4a5842d5a62f7"));
        dr.add(new Digest("sha-1", "2d3d054a0b2595cdb79b0ca3976e18021d014540"));
        dr.add(new Digest("md5", "29e4c85820ae4535741b2f3b6a3aeb3e"));
        knownResults.put("testdata/testfile10.bin", dr);
        
        return knownResults;
    }
}
