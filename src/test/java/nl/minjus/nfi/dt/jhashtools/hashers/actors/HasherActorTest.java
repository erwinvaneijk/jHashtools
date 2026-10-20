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
