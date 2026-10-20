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

package nl.minjus.nfi.dt.jhashtools.hashers.actors;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import nl.minjus.nfi.dt.jhashtools.hashers.DigestAlgorithmFactory;

public class HasherActorTest
{
    private final Fiber fiber = new ThreadFiber();

    @AfterEach
    public void tearDown() {
        fiber.dispose();
    }

    private HasherActor createHasherActor(final Set<File> theClaimedFiles) throws Exception {
        return new HasherActor(List.of(DigestAlgorithmFactory.create("sha-256")), new MemoryChannel<>(),
            new MemoryChannel<>(), new MemoryChannel<>(), new MemoryChannel<>(), fiber, theClaimedFiles);
    }

    @Test
    public void testFileIsHashedByOneHasherOnly() throws Exception {
        final Set<File> claimedFiles = ConcurrentHashMap.newKeySet();
        final HasherActor firstHasher = createHasherActor(claimedFiles);
        final HasherActor secondHasher = createHasherActor(claimedFiles);
        final File file = new File("testdata/testfile1.bin");

        final DigestMessage firstMessage = firstHasher.act(file);
        final DigestMessage secondMessage = secondHasher.act(file);

        assertThat(firstMessage.getFile()).isEqualTo(file);
        assertThat(firstMessage.getDigestResult()).isNotEmpty();
        assertThat(secondMessage).isNull();
    }

    @Test
    public void testEndOfInputIsNotHashed() throws Exception {
        final HasherActor hasher = createHasherActor(ConcurrentHashMap.newKeySet());

        final DigestMessage message = hasher.act(null);

        assertThat(message).isNull();
    }
}
