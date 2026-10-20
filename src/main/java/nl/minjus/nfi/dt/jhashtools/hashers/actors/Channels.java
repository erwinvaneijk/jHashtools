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
import org.jetlang.channels.MemoryChannel;

/**
 * This class contains all the channels.
 *
 * @author Erwin van Eijk
 *
 */
public final class Channels
{
    private final Channel<File> filenameChannel = new MemoryChannel<File>();
    private final Channel<Void> filenameStopChannel = new MemoryChannel<Void>();
    private final Channel<DigestMessage> digestChannel = new MemoryChannel<DigestMessage>();
    private final Channel<Void> digestStopChannel = new MemoryChannel<Void>();
    private final Channel<File> pathChannel = new MemoryChannel<File>();
    private final Channel<Void> pathStopChannel = new MemoryChannel<Void>();

    /**
     * @return the filenameChannel
     */
    public Channel<File> getFilenameChannel() {
        return filenameChannel;
    }
    /**
     * @return the filenameStopChannel
     */
    public Channel<Void> getFilenameStopChannel() {
        return filenameStopChannel;
    }
    /**
     * @return the digestChannel
     */
    public Channel<DigestMessage> getDigestChannel() {
        return digestChannel;
    }
    /**
     * @return the digestStopChannel
     */
    public Channel<Void> getDigestStopChannel() {
        return digestStopChannel;
    }
    /**
     * @return the pathChannel
     */
    public Channel<File> getPathChannel() {
        return pathChannel;
    }
    /**
     * @return the pathStopChannel
     */
    public Channel<Void> getPathStopChannel() {
        return pathStopChannel;
    }
}
