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
