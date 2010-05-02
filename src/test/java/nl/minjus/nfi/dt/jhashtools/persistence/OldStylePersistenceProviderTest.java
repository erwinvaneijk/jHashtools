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

package nl.minjus.nfi.dt.jhashtools.persistence;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class OldStylePersistenceProviderTest {

    @Test
    public void testLoadFile() {
        try {
            File file = new File("testdata/oldformat.txt");
            InputStream input = new FileInputStream(file);
            OldStylePersistenceProvider provider = new OldStylePersistenceProvider();
            DirHasherResult result = (DirHasherResult) provider.load(input, DirHasherResult.class);
            assert result != null;
            assertEquals(4, result.size());
            assertEquals("md5:a4850cd827a34a7e54dacf6814e06f55", result.get(new File("hashtree256.py")).getDigest("md5").toString());
            assertEquals("sha-1:23e7ace892b507b07e4dfcf1f028ee3130bc682e", result.get(new File("hashtree256.py")).getDigest("sha-1").toString());

            assertEquals("md5:44af6da725a24c2d8363a42069ee110f", result.get(new File("shatest.py")).getDigest("md5").toString());
            assertEquals("sha-256:b7e94899a85df9809030e8ede16b857e90d886279dc1d3d14562142c9303dc39", result.get(new File("shatest.py")).getDigest("sha-256").toString());

        } catch (IOException ex) {
            fail("We got an IOException. Wrong.");
        } catch (PersistenceException e) {
            fail(e.toString());
        }
    }
}
