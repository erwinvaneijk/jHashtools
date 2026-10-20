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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a Comparator for File where each file is deemed the same
 * of the path would point to the same file regardless of naming.
 */
class FileComparator implements Comparator<File>
{
    private static final Logger LOG = LoggerFactory.getLogger(FileComparator.class);

    private static final float CACHE_FILL_PERCENTAGE = 0.75f;

    private static final int DEFAULT_FILENAME_CACHE_SIZE = 1024;

    /**
     * A cache for the filenames, to save a lot of lookups.
     */
    private static Map<File, String> filenameCache = new LinkedHashMap<File, String>(
        DEFAULT_FILENAME_CACHE_SIZE, CACHE_FILL_PERCENTAGE, true)
    {
        private static final long serialVersionUID = 1;

        @Override
        protected boolean removeEldestEntry(final Map.Entry<File, String> eldest) {
            return size() > DEFAULT_FILENAME_CACHE_SIZE;
        }
    };

    /**
     * true if we ignore the case in the names that pass by.
     */
    private boolean ignoreCase;

    public FileComparator()
    {
        this.ignoreCase = false;
    }

    public FileComparator(boolean isIgnoringCase)
    {
        this.ignoreCase = isIgnoringCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public boolean isIgnoringCase() {
        return this.ignoreCase;
    }

    @Override
    public int compare(final File o1, final File o2) {
        try {
            final String filename1 = getNormalizedPath(o1);
            final String filename2 = getNormalizedPath(o2);
            if (this.isIgnoringCase()) {
                return filename1.compareToIgnoreCase(filename2);
            } else {
                return filename1.compareTo(filename2);
            }
        } catch (IOException ex) {
            return o1.compareTo(o2);
        }
    }

    /**
     * Get a normalized path for a file without resolving symbolic links.
     * This normalizes the path by resolving . and .. segments, but does not
     * follow symbolic links.
     *
     * @param file
     *            the file to get the normalized path for
     * @return the normalized path
     * @throws IOException
     *             if the file does not exist
     */
    private String getNormalizedPath(final File file) throws IOException {
        String path = filenameCache.get(file);
        if (path == null) {
            path = normalizePath(file);
            filenameCache.put(file, path);
            return path;
        }
        return path;
    }

    /**
     * Normalize a file path without resolving symbolic links.
     * Uses getCanonicalPath() for non-symlinks (which normalizes . and ..),
     * and getAbsolutePath() for symlinks (to avoid following them).
     * For non-existent files, uses absolute path with manual normalization.
     *
     * @param file
     *            the file to normalize
     * @return the normalized path
     * @throws IOException
     *             if the path cannot be determined
     */
    private String normalizePath(final File file) throws IOException {
        if (file == null) {
            return "";
        }

        // For non-existent files, canonical path resolution may fail
        // Try canonical path first (normalizes paths for existing files)
        try {
            // Check if this is a symbolic link
            if (file.exists() && isSymlink(file)) {
                // For symlinks, use absolute path to avoid resolving the link
                return file.getAbsolutePath();
            }
            // For non-symlinks, use canonical path for normalization
            return file.getCanonicalPath();
        } catch (final IOException e) {
            // If canonical path fails, fall back to absolute path
            LOG.debug("Could not get canonical path for {}, using absolute: {}",
                file, e.getMessage());
            return file.getAbsolutePath();
        }
    }

    /**
     * Check if a file is a symbolic link.
     * Uses Files.isSymbolicLink() which checks the file itself,
     * not its parent paths.
     *
     * @param file
     *            the file to check (must exist)
     * @return true if the file is a symbolic link
     */
    private boolean isSymlink(final File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        try {
            return Files.isSymbolicLink(file.toPath());
        } catch (final SecurityException e) {
            LOG.warn("Could not determine if file is symlink: {}", file, e);
            return false;
        }
    }
}
