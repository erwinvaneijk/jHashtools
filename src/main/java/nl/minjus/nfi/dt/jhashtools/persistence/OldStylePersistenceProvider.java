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

package nl.minjus.nfi.dt.jhashtools.persistence;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Calendar;
import java.util.Map;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.ConcurrentFileHasher;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class supports the Old Style hashes.txt files that are now generated using JSON. The old format should be
 * abolished, but backwards compatibility is a must.
 *
 * @author Erwin van Eijk
 */
public class OldStylePersistenceProvider implements PersistenceProvider
{

    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentFileHasher.class);

    @Override
    public void persist(final OutputStream out, final Object obj) throws PersistenceException {
        if (obj != null && obj instanceof DirHasherResult) {
            final DirHasherResult directoryHasherResult = (DirHasherResult) obj;
            final PrintStream stream = new PrintStream(out);
            stream.println("Generated with: " + directoryHasherResult.getConstructionInfo().toString() + " "
                + Calendar.getInstance().getTime().toString());
            for (final Map.Entry<File, DigestResult> entry : directoryHasherResult) {
                stream.println(entry.getKey().toString());
                for (final Digest d : entry.getValue()) {
                    stream.println("\t" + d.prettyPrint(":\t"));
                }
            }
        } else {
            if (obj != null) {
                throw new PersistenceException("There is no persistence method defined for class "
                    + obj.getClass().toString());
            } else {
                throw new PersistenceException("There is no persistence method defined for null class");
            }
        }
    }

    @Override
    public <T> T load(final Reader reader, final TypeReference<T> typeReference) throws PersistenceException {
        throw new PersistenceException(
            "Currently the use of TypeReference is not supported for oldstyle files");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T load(final Reader reader, final Class<T> clazz) throws PersistenceException {
        if (clazz.equals(DirHasherResult.class)) {
            final DirHasherResult directoryHasherResult = new DirHasherResult();
            try {
                final LineNumberReader lineNumberReader = new LineNumberReader(reader);

                String line = lineNumberReader.readLine();
                if (line == null || !line.startsWith("Generated with: ")) {
                    throw new PersistenceException("The first line is not ok.");
                }
                String currentFileName = "";
                DigestResult result = new DigestResult();

                while (true) {
                    line = lineNumberReader.readLine();
                    if (line == null) {
                        if (!currentFileName.isEmpty()) {
                            directoryHasherResult.put(currentFileName, result);
                        }
                        break;
                    }

                    if (line.charAt(0) != '\t') {
                        if (!currentFileName.isEmpty()) {
                            directoryHasherResult.put(currentFileName, result);
                        }
                        currentFileName = line;
                        result = new DigestResult();
                    } else {
                        try {
                            final OldStyleHashesParser parser = createParser(line);
                            final Digest theDigest = parser.digest();
                            result.add(theDigest);
                        } catch (final RecognitionException ex) {
                            LOG.info("Could not recognize [" + line + "]");
                        }
                    }
                }
                return (T) directoryHasherResult;
            } catch (final IOException ex) {
                throw new PersistenceException(ex);
            }
        } else {
            throw new PersistenceException("There is no persistence method defined for class"
                + clazz.toString());
        }
    }

    private OldStyleHashesParser createParser(final String testString) throws IOException {
        final CharStream stream = new ANTLRStringStream(testString);
        final OldStyleHashesLexer lexer = new OldStyleHashesLexer(stream);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new OldStyleHashesParser(tokens);
    }
}
