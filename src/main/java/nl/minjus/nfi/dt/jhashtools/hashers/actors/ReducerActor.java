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

import org.jetlang.channels.Channel;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

/**
 * An actor that can reduce all the computed digests into one
 * single <code>DirHasherResult</code>.
 *
 * @author Erwin van Eijk
 *
 */
public class ReducerActor extends Actor<DigestMessage, Void>
{
    private static final Logger LOG = LoggerFactory.getLogger(ReducerActor.class.getName());

    private final DirHasherResult dirHasherResult;

    public ReducerActor(final DirHasherResult dirHasherResult,
        final Channel<DigestMessage> inbox,
        final Channel<Void> stopChannel,
        final Fiber fiber)
    {
        super(inbox, null, stopChannel, null, fiber);
        this.dirHasherResult = dirHasherResult;
    }

    @Override
    public Void act(final DigestMessage request) {
        if ((request != null) && (request.getFile() != null)) {
            LOG.debug("Got a message " + request.getFile());
            dirHasherResult.put(request.getFile(), request.getDigestResult());
            dirHasherResult.putAccessTimes(request.getFile(), request.getAccessTimes());
            LOG.debug("#" + dirHasherResult.size());
        } else {
            LOG.debug("Null message");
        }
        return null;
    }

}
