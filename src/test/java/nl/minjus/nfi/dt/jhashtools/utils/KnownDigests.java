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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.minjus.nfi.dt.jhashtools.utils;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * Utility class.
 * 
 * @author Erwin van Eijk
 */
public class KnownDigests
{

    /**
     * Get a list of all the known digests in the test folder.
     * 
     * @return a DirHasherResult.
     */
    public static DirHasherResult getKnownResults() {
        DirHasherResult knownResults = new DirHasherResult();

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

        if (!OsUtils.isWindows()) {
            dr = new DigestResult();
            dr.add(new Digest("sha-256", "aeb2027f7ee48ae11db86203a93a05a2539af4a6cd46268941e2c8f4c4dfb35a"));
            dr.add(new Digest("sha-1", "5985b3d0f4a8126803c9ddf7811dde4e38a86af4"));
            dr.add(new Digest("md5", "af5d3dfecc364ff1bc1177d24ada7137"));
            knownResults.put("testdata/oldformat.txt", dr);

            dr = new DigestResult();
            dr.add(new Digest("md5", "0efcde993ca6f8bcd71671dfe927788c"));
            dr.add(new Digest("sha-1", "b4497e1822a51079df54b54e2e38a8262d752851"));
            dr.add(new Digest("sha-256", "220408cd9b16d0b025fb0c822a70b340b02f1344e59b19ddff8deef0cc6e7031"));
            knownResults.put("testdata/include-md5-sha1.txt", dr);

            dr = new DigestResult();
            dr.add(new Digest("md5", "bed8e0d55ab120d38325af63da19125f "));
            dr.add(new Digest("sha-1", "2b3a601a1ee759eec30ddcde458d459aa26ba78f"));
            dr.add(new Digest("sha-256", "8072761bd9f0147042bed6f2328f62c964707baa2cfb5cc449bd376b55ff525e"));
            knownResults.put("testdata/include-sha1-sha256-sha512.txt", dr);
        } else {
            dr = new DigestResult();
            dr.add(new Digest("sha-256", "750cf3af02b00f2d6aad9802f4f7be981973fc53e1e117459b267a0983a3ba2d"));
            dr.add(new Digest("sha-1", "f4868bbac9c2c218a24a3b5eb430c223e0c741da"));
            dr.add(new Digest("md5", "cbbcd8f0812799b12ebb5f645a7bdb85"));
            knownResults.put("testdata/oldformat.txt", dr);

            dr = new DigestResult();
            dr.add(new Digest("md5", "78dbebeea000b5e6735308a1db4994d9"));
            dr.add(new Digest("sha-1", "b67be08991d5d0a826bf01bc9110b3bdbe33c474"));
            dr.add(new Digest("sha-256", "483dec6fb91c1a6142928d28c1f166b62e38c04c2a659022ec1e63f016078867"));
            knownResults.put("testdata/include-md5-sha1.txt", dr);

            dr = new DigestResult();
            dr.add(new Digest("md5", "504c08283bc87e86a6ed327838c10f48"));
            dr.add(new Digest("sha-1", "e229a5114d7476505210778a5a5fcc94d69e36c1"));
            dr.add(new Digest("sha-256", "d19924db3a6f293a2449f9df4e056d5165fb98d0432c69e8f5ee740ffa92a5d2"));
            knownResults.put("testdata/include-sha1-sha256-sha512.txt", dr);

        }
        return knownResults;
    }
}
