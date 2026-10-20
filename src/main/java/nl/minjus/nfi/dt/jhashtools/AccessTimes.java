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

package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

/**
 * The last access times of a file, observed just before and just after its content was read for hashing.
 *
 * @param before
 *            the last access time before the file was read.
 * @param after
 *            the last access time after the file was read.
 */
public record AccessTimes(FileTime before, FileTime after)
{
    /**
     * Constructor.
     *
     * @param before
     *            the last access time before the file was read.
     * @param after
     *            the last access time after the file was read.
     */
    public AccessTimes
    {
        Objects.requireNonNull(before, "before");
        Objects.requireNonNull(after, "after");
    }

    /**
     * Read the last access time of <code>aFile</code> without following symbolic links. Reading the attributes does
     * not change the access time itself.
     *
     * @param aFile
     *            the file to inspect.
     * @return the last access time.
     * @throws IOException
     *             when the attributes cannot be read.
     */
    public static FileTime lastAccessTime(final File aFile) throws IOException {
        return Files.readAttributes(aFile.toPath(), BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS)
            .lastAccessTime();
    }

    /**
     * Whether reading the file changed its last access time.
     *
     * @return true if the access time after reading differs from the one before.
     */
    public boolean changed() {
        return !this.before.equals(this.after);
    }
}
