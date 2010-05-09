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

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

/**
 * This class implements a Comparator for File where each file
 * is deemed the same of the path would point to the same file regardless of
 * how it is named.
 */
class FileComparator implements Comparator<File> {

    private boolean ignoreCase;

    public FileComparator() {
        this.ignoreCase = false;
    }

    public FileComparator(boolean isIgnoringCase) {
        this.ignoreCase = isIgnoringCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoringCase() {
        return this.ignoreCase;
    }

    @Override
    public int compare(File o1, File o2) {
        try {
            String filename1 = o1.getCanonicalPath();
            String filename2 = o2.getCanonicalPath();
            if (this.isIgnoringCase()) {
                return filename1.compareToIgnoreCase(filename2);
            } else {
                return filename1.compareTo(filename2);
            }
        } catch (IOException ex) {
            return o1.compareTo(o2);
        }
    }
}
