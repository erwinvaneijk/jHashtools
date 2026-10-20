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

package nl.minjus.nfi.dt.jhashtools.persistence;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Calendar;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import nl.minjus.nfi.dt.jhashtools.Digest;
import nl.minjus.nfi.dt.jhashtools.DigestResult;
import nl.minjus.nfi.dt.jhashtools.DirHasherResult;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;

/**
 * This class supports the Old Style hashes.txt files that are now generated using JSON. The old format should be
 * abolished, but backwards compatibility is a must.
 *
 * @author Erwin van Eijk
 */
public class OldStylePersistenceProvider implements PersistenceProvider
{

    /**
     * Turns every lexer and parser error into an exception, instead of the default recovery that prints to the
     * console and continues with a partial result.
     */
    private static final BaseErrorListener FAIL_ON_SYNTAX_ERROR = new BaseErrorListener()
    {
        @Override
        public void syntaxError(final Recognizer<?, ?> recognizer, final Object offendingSymbol, final int line,
            final int charPositionInLine, final String msg, final RecognitionException e)
        {
            throw new ParseCancellationException(msg, e);
        }
    };

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
                            result.add(parser.digest().digestResult);
                        } catch (final ParseCancellationException ex) {
                            throw new PersistenceException("Could not parse the digest on line "
                                + lineNumberReader.getLineNumber() + ": [" + line.trim() + "]", ex);
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
        final CharStream stream = CharStreams.fromString(testString);
        final OldStyleHashesLexer lexer = new OldStyleHashesLexer(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(FAIL_ON_SYNTAX_ERROR);
        final OldStyleHashesParser parser = new OldStyleHashesParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(FAIL_ON_SYNTAX_ERROR);
        parser.setErrorHandler(new BailErrorStrategy());
        return parser;
    }
}
