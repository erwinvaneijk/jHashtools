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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a Comparator for File where each file is deemed the same of the path would point to the same
 * file regardless of how it is named.
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
    public int compare(File o1, File o2) {
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
     * @param file the file to get the normalized path for
     * @return the normalized path
     * @throws IOException if the file does not exist
     */
    private String getNormalizedPath(File file) throws IOException {
        String path = filenameCache.get(file);
        if (path == null) {
            path = normalizePath(file);
            filenameCache.put(file, path);
            return path;
        } else {
            return path;
        }
    }

    /**
     * Normalize a file path without resolving symbolic links.
     * Uses getCanonicalPath() for non-symlinks (which normalizes . and ..),
     * and getAbsolutePath() for symlinks (to avoid following them).
     * For non-existent files, uses absolute path with manual normalization.
     *
     * @param file the file to normalize
     * @return the normalized path
     * @throws IOException if the path cannot be determined
     */
    private String normalizePath(File file) throws IOException {
        if (file == null) {
            return "";
        }
        
        // For non-existent files, canonical path resolution may fail or not work as expected
        // Try canonical path first (works for existing files and normalizes paths)
        try {
            // Check if this is a symbolic link (only makes sense for existing files)
            if (file.exists() && isSymlink(file)) {
                // For symlinks, use absolute path to avoid resolving the link
                return file.getAbsolutePath();
            }
            // For non-symlinks (or non-existent files), use canonical path for normalization
            return file.getCanonicalPath();
        } catch (IOException e) {
            // If canonical path fails, fall back to absolute path
            LOG.debug("Could not get canonical path for {}, using absolute path: {}", file, e.getMessage());
            return file.getAbsolutePath();
        }
    }

    /**
     * Check if a file is a symbolic link.
     * Uses Files.isSymbolicLink() which checks the file itself, not its parent paths.
     *
     * @param file the file to check (must exist)
     * @return true if the file is a symbolic link
     */
    private boolean isSymlink(final File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        try {
            return Files.isSymbolicLink(file.toPath());
        } catch (SecurityException e) {
            LOG.warn("Could not determine if file is symlink (permission denied): {}", file, e);
            return false;
        }
    }
}
