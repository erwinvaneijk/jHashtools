/*
 * Copyright (c) 2012 Erwin van Eijk <erwin.vaneijk@gmail.com>.
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

package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import java.io.File;

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generate <code>File</code> objects to use for further processing.
 *
 * @author Erwin van Eijk
 */
public class FileNameGenerator extends Actor<File, File>
{
    private static final Logger LOG = LoggerFactory.getLogger(FileNameGenerator.class);

    public FileNameGenerator(final Channel<File> inbox,
        final Channel<File> outbox,
        final Channel<Void> stopChannel,
        final Channel<Void> nextStopChannel,
        final Fiber fiber)
    {
        super(inbox, outbox, stopChannel, nextStopChannel, fiber);
    }

    @Override
    public File act(final File startPath) {
        LOG.debug("Starting on {}", startPath);
        walkTheFile(startPath);
        return null;
    }

    private void walkTheFile(final File aPath) {
        if (aPath == null || !aPath.exists()) {
            return;
        }
        if (aPath.isFile()) {
            emit(aPath);
        } else if (aPath.isDirectory()) {
            for (final File child : aPath.listFiles()) {
                if (child.isFile()) {
                    emit(child);
                } else {
                    walkTheFile(child);
                }
            }
        }
    }

    @Override
    protected void emit(final File theFile) {
        LOG.debug("Yielding file {}", theFile);
        super.emit(theFile);
    }
}
