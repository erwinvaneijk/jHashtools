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

package nl.minjus.nfi.dt.jhashtools.hashers;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

/**
 * @author Erwin van Eijk
 */
class SerialDirectoryHasher extends AbstractDirectoryHasher
{

    private final DirHasherResult results = new DirHasherResult();

    public SerialDirectoryHasher()
    {
        super();
    }

    public SerialDirectoryHasher(String algorithm) throws NoSuchAlgorithmException
    {
        super(algorithm);
    }

    public SerialDirectoryHasher(Collection<String> algorithms) throws NoSuchAlgorithmException
    {
        super(algorithms);
    }

    @Override
    public DirHasherResult getDigests(final File startFile)
    {
        this.results.clear();
        updateDigests(this.results, startFile);
        return this.results;
    }

    @Override
    public void updateDigests(DirHasherResult digests, final File file)
    {
        if (!file.exists()) {
            throw new IllegalArgumentException(String.format("File %s does not exist", file.toString()));
        }

        try {
            FileWalker walker = new FileWalker();
            DirectoryVisitor visitor = new DirectoryVisitor(algorithms, digests);
            visitor.setVerbose(this.isVerbose());
            walker.addWalkerVisitor(visitor);
            walker.walk(file);
        } catch (NoSuchAlgorithmException ex) {
            // this cannot happen. FLW.
        }
    }
}
