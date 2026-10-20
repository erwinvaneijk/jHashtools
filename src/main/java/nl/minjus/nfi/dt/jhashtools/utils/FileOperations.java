/*
 * Copyright (c) 2010 Erwin van Eijk <erwin.vaneijk@gmail.com>.
 *
 * Licensed under the EUPL, Version 1.2 only (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 *
 * SPDX-License-Identifier: EUPL-1.2
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
