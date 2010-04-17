/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kojak
 */
public class KnownDigests {

    public final static Map<String, Map<Integer, String>> getKnownDigests() {
        Map<String, Map<Integer, String>> knownDigests = new HashMap<String, Map<Integer, String>>();

        Map<Integer, String> expectedSha256Digests = new HashMap<Integer, String>();

        expectedSha256Digests.put(1, "589f2fc47ebe5744c2c349a8529dde37218eac8782e4b54bbbabd46bcbdd62ae");
        expectedSha256Digests.put(2, "615e3b6489843b9f7fb1a3261918a2b3034a5950972bf09a568b6857aa173154");
        expectedSha256Digests.put(3, "d0bdb2f79e6c1298c16ce49aa43686d8e9a2364dc3155a0fc3db7e62cbb75069");
        expectedSha256Digests.put(4, "b39239ce8959672b30a39d2f9ce95e4cfb8c4c531891132d7cdcd8c8684ddf8b");
        expectedSha256Digests.put(5, "5ebe81f8113d4bfeb65ef61606018b77e3c1a682243bf94c34e33e42a48e06c4");
        expectedSha256Digests.put(6, "bdcba8cb46bf497dfc89f4e4d94e20b567526a1d64497459ef5224543b878905");
        expectedSha256Digests.put(7, "1f6ee38218bc6fce9f388f75c0900fc68a3e8aacede26117914e765e9cd08d4e");
        expectedSha256Digests.put(8, "cf88ce7c5bc7e53e980b1eabe633792913fadbea292d665bc56398ccca5dbc85");
        expectedSha256Digests.put(9, "f2bb1df1fac49c804e9cfe5d56cbc9c3865b045656d7f68237efa9464683206f");
        expectedSha256Digests.put(10, "a4d68e8ee6d7c8a11913bf9703f29ff9082b96eace52cddab1c4a5842d5a62f7");

        knownDigests.put("sha-256", expectedSha256Digests);

        Map<Integer, String> expectedShaDigests = new HashMap<Integer, String>();
        expectedShaDigests.put(1, "f31ce0511a64b48dd2ba82f2ef5ae3186c4c9440");
        expectedShaDigests.put(2, "39c8bb6db73ddcbe755e25c276e81904cadeabfc");
        expectedShaDigests.put(3, "4ac5981591e448efac073c4232a805022b3ed295");
        expectedShaDigests.put(4, "7b96266c96b18a44dec6f52695aeb8957a8ece86");
        expectedShaDigests.put(5, "a3ff2d0dee09e8481d48448e5889305ee34a1789");
        expectedShaDigests.put(6, "a374595ab7cf3ba691e3e4389549a5aad49f7a46");
        expectedShaDigests.put(7, "b7807b4958df45e4cb7ea2aa435d6029096c8094");
        expectedShaDigests.put(8, "1187ba416ae93ef3be40490171e6f76c4bf933ca");
        expectedShaDigests.put(9, "7c8b4cbd546ec4a3b0314d67d0981a7ea907c356");
        expectedShaDigests.put(10, "2d3d054a0b2595cdb79b0ca3976e18021d014540");

        knownDigests.put("sha-1", expectedShaDigests);
        
        return knownDigests;
    }
}
