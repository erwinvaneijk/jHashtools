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

package nl.minjus.nfi.dt.jhashtools.hashers;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * This interface should be implemented by clients that advocate the
 * means to walk around a file system and can compute digests on the files
 * in the file system.
 *
 * @author Erwin van Eijk
 */
public interface DirectoryHasher
{
    void addAlgorithm(String algorithm) throws NoSuchAlgorithmException;

    void setAlgorithms(Collection<String> algorithms) throws NoSuchAlgorithmException;

    Collection<String> getAlgorithms();

    void setVerbose(boolean verbose);

    boolean isVerbose();

    /**
     * Get all the digests for all configured algorithms from all the files that
     * are reachable from <code>startFile</code>.
     *
     * @param startFile
     *          Where to start looking for new files. Search is short if startFile
     *          points to an ordinary file, instead of a directory.
     * @return a DirHasherResult
     *          This will contain all the digests for all the files.
     */
    DirHasherResult getDigests(File startFile);

    void updateDigests(DirHasherResult digests, File file);
}
