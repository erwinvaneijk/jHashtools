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
