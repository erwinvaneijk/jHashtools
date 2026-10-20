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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nl.minjus.nfi.dt.jhashtools.utils.OsUtils;

public class AccessTimesTest
{
    private static final FileTime EARLIER = FileTime.from(Instant.parse("2001-02-03T04:05:06Z"));
    private static final FileTime LATER = FileTime.from(Instant.parse("2002-03-04T05:06:07Z"));

    @TempDir
    Path tempFolder;

    @Test
    public void testUnchangedWhenBeforeEqualsAfter() {
        final AccessTimes accessTimes = new AccessTimes(EARLIER, FileTime.from(EARLIER.toInstant()));

        assertThat(accessTimes.changed()).isFalse();
    }

    @Test
    public void testChangedWhenBeforeDiffersFromAfter() {
        final AccessTimes accessTimes = new AccessTimes(EARLIER, LATER);

        assertThat(accessTimes.changed()).isTrue();
    }

    @Test
    public void testRejectsMissingTimes() {
        assertThatNullPointerException().isThrownBy(() -> new AccessTimes(null, LATER));
        assertThatNullPointerException().isThrownBy(() -> new AccessTimes(EARLIER, null));
    }

    @Test
    public void testLastAccessTimeReadsTheAccessTime() throws IOException {
        final Path file = Files.writeString(tempFolder.resolve("file.txt"), "content");
        Files.getFileAttributeView(file, BasicFileAttributeView.class).setTimes(null, EARLIER, null);

        final FileTime lastAccessTime = AccessTimes.lastAccessTime(file.toFile());

        assertThat(lastAccessTime).isEqualTo(EARLIER);
    }

    @Test
    public void testLastAccessTimeDoesNotFollowSymbolicLinks() throws IOException {
        assumeThat(OsUtils.isWindows()).as("symbolic links need privileges on Windows").isFalse();
        final Path target = Files.writeString(tempFolder.resolve("target.txt"), "content");
        final Path link = Files.createSymbolicLink(tempFolder.resolve("link.txt"), target);
        Files.getFileAttributeView(target, BasicFileAttributeView.class).setTimes(null, EARLIER, null);
        Files.getFileAttributeView(link, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS)
            .setTimes(null, LATER, null);

        final FileTime lastAccessTime = AccessTimes.lastAccessTime(link.toFile());

        assertThat(lastAccessTime).isEqualTo(LATER);
    }
}
