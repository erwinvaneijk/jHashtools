/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.persistence.JsonPersister;
import nl.minjus.nfi.dt.jhashtools.persistence.Persist;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kojak
 */
public class DirHasherResultVerifier {

    private final DirHasherResult measuredDigests;
    private DirHasherResult verificationDigests;

    public DirHasherResultVerifier(DirHasherResult result) {
        this.measuredDigests = result;
    }

    public void loadDigestsFromFile(String filename) {
        DirHasherResult result = null;
        InputStream stream;
        try {
            File inputFile = new File(filename);
            stream = new FileInputStream(inputFile);
            Persist persist = new JsonPersister();
            result = (DirHasherResult) persist.load(stream, DirHasherResult.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.verificationDigests = result;
        }
    }

    public void verify(PrintStream out) {
        DirHasherResult differences = this.verificationDigests.notIntersect(this.measuredDigests);
        if (differences.size() == 0) {
            out.println("There are no differences.");
        } else {
            if (differences.containsKey("./hashes.txt")) {
                out.println("There are no differences.");
                out.println("hashes.txt:");
                for (Digest d : differences.get("./hashes.txt")) {
                    out.printf("\t%s\n", d.toString());
                }
            } else {
                out.println("There are differences.");
                for (Map.Entry<String, DigestResult> entry : differences.entrySet()) {
                    out.println(entry.getKey());
                    for (Digest d : entry.getValue()) {
                        out.printf("\t%s\n", d.toString('\t'));
                    }
                }
            }
        }
    }
}