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

package nl.minjus.nfi.dt.jhashtools.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for operations on files.
 *
 * @author Erwin van Eijk
 */
public class FileOperations
{
    /**
     * Determine whether or not aFirstFile and aSecondFile really are the same file.
     *
     * @param aFirstFile
     *            the first file to compare
     * @param aSecondFile
     *            the second file to compare
     * @return true if they access the same file.
     */
    public static boolean isSameFile(final File aFirstFile, final File aSecondFile) {
        try {
            if (aFirstFile.exists() && aSecondFile.exists()) {
                if (aFirstFile.getCanonicalFile().equals(aSecondFile.getCanonicalFile())) {
                    return true;
                }
            }
        } catch (final IOException ex) {
            return false;
        }
        return false;
    }

    /**
     * Determine whether or not aFirstFile and aSecondFile really are the same file.
     *
     * @param aFirstFile
     *            the first file to compare
     * @param aSecondFile
     *            the second file to compare to.
     * @return true of they point to the same file.
     */
    public static boolean isSameFile(final String aFirstFile, final String aSecondFile) {
        final File firstFile = new File(aFirstFile);
        final File secondFile = new File(aSecondFile);
        return FileOperations.isSameFile(firstFile, secondFile);
    }

    /**
     * Determine whether writing to <code>aCandidate</code> would write inside <code>aDirectory</code>, or overwrite
     * <code>aDirectory</code> itself when it is a file. Symbolic links are resolved, so a link pointing into
     * <code>aDirectory</code> counts as inside it.
     *
     * @param aCandidate
     *            the path to check, which does not have to exist.
     * @param aDirectory
     *            the directory or file to check against.
     * @return true if <code>aCandidate</code> is <code>aDirectory</code> or lies below it; false if
     *         <code>aDirectory</code> does not exist.
     * @throws IOException
     *             when a path cannot be resolved.
     */
    public static boolean isWithin(final Path aCandidate, final Path aDirectory) throws IOException {
        if (!Files.exists(aDirectory)) {
            return false;
        }
        final Path absoluteCandidate = aCandidate.toAbsolutePath().normalize();
        final Path parent = absoluteCandidate.getParent();
        final Path candidate;
        if (Files.exists(absoluteCandidate)) {
            candidate = absoluteCandidate.toRealPath();
        } else if (parent != null && Files.exists(parent)) {
            candidate = parent.toRealPath().resolve(absoluteCandidate.getFileName());
        } else {
            candidate = absoluteCandidate;
        }
        return candidate.startsWith(aDirectory.toRealPath());
    }
}
