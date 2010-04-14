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

    public final static Map<Integer, String> getKnownDigests() {
        Map<Integer, String> expectedDigests = new HashMap<Integer, String>();
        
        expectedDigests.put(1, "589f2fc47ebe5744c2c349a8529dde37218eac8782e4b54bbbabd46bcbdd62ae");
        expectedDigests.put(2, "615e3b6489843b9f7fb1a3261918a2b3034a5950972bf09a568b6857aa173154");
        expectedDigests.put(3, "d0bdb2f79e6c1298c16ce49aa43686d8e9a2364dc3155a0fc3db7e62cbb75069");
        expectedDigests.put(4, "b39239ce8959672b30a39d2f9ce95e4cfb8c4c531891132d7cdcd8c8684ddf8b");
        expectedDigests.put(5, "5ebe81f8113d4bfeb65ef61606018b77e3c1a682243bf94c34e33e42a48e06c4");
        expectedDigests.put(6, "bdcba8cb46bf497dfc89f4e4d94e20b567526a1d64497459ef5224543b878905");
        expectedDigests.put(7, "1f6ee38218bc6fce9f388f75c0900fc68a3e8aacede26117914e765e9cd08d4e");
        expectedDigests.put(8, "cf88ce7c5bc7e53e980b1eabe633792913fadbea292d665bc56398ccca5dbc85");
        expectedDigests.put(9, "f2bb1df1fac49c804e9cfe5d56cbc9c3865b045656d7f68237efa9464683206f");
        expectedDigests.put(10, "a4d68e8ee6d7c8a11913bf9703f29ff9082b96eace52cddab1c4a5842d5a62f7");

        return expectedDigests;
    }
}
