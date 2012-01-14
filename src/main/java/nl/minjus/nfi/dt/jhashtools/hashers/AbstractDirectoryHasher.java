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

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;

import nl.minjus.nfi.dt.jhashtools.DirHasherResult;

abstract class AbstractDirectoryHasher implements DirectoryHasher
{
    protected final Collection<DigestAlgorithm> algorithms = new LinkedList<DigestAlgorithm>();
    private boolean verbose;

    public AbstractDirectoryHasher()
    {
    }

    public AbstractDirectoryHasher(String algorithm) throws NoSuchAlgorithmException
    {
        this.addAlgorithm(algorithm);
    }

    public AbstractDirectoryHasher(Collection<String> algorithms) throws NoSuchAlgorithmException
    {
        for (String algorithm : algorithms) {
            this.addAlgorithm(algorithm);
        }
    }

    public void setAlgorithms(Collection<String> algorithms) throws NoSuchAlgorithmException
    {
        this.algorithms.clear();
        for (String algorithm : algorithms) {
            addAlgorithm(algorithm);
        }
    }

    public void addAlgorithm(String algorithm) throws NoSuchAlgorithmException
    {
        algorithms.add(DigestAlgorithmFactory.create(algorithm));
    }

    public abstract DirHasherResult getDigests(File startFile);

    public abstract void updateDigests(DirHasherResult digests, File file);

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public boolean isVerbose()
    {
        return this.verbose;
    }

    public final Collection<String> getAlgorithms()
    {
        Collection<String> newSet = new LinkedList<String>();
        for (DigestAlgorithm alg: this.algorithms) {
            newSet.add(alg.getName());
        }
        return newSet;
    }
}
