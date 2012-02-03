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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.hashers.ConcurrencyMode;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasher;
import nl.minjus.nfi.dt.jhashtools.hashers.DirectoryHasherCreator;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;
import nl.minjus.nfi.dt.jhashtools.utils.Version;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point of the cli version of the tooling.
 *
 * @author Erwin van Eijk
 */
public class App
{

    private static final int EXIT_PERSISTENCE_ERROR = 2;
    private static final String USAGE = "[options] dir [dir...]";
    private static final String HEADER = "hashtree - Creating a list of digests for files "
        + "and/or directories.\nCopyright (c) 2010 - 2012, Erwin van Eijk";
    private static final String FOOTER = "";
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private static final int DEFAULT_TERMINAL_WIDTH = 80;
    private static final String DEFAULT_ALGORITHM = "sha-256";

    public static void main(final String[] arguments) {
        LOG.info("Version: " + Version.getVersion());
        final long startTime = System.currentTimeMillis();
        
        final CommandLine line = App.getCommandLine(arguments);
        
        final String[] filesToProcess = line.getArgs();

        final DirectoryHasher directoryHasher = createDirectoryHasher(line);

        
        if (line.hasOption("i") && line.hasOption("o")) {
            LOG.warn("Make up your mind. Cannot do -i and -o at the same time.");
            System.exit(1);
        }

        final PersistenceStyle persistenceStyle = getPersistenceStyle(line);
        if (line.hasOption("i")) {
            final String filename = line.getOptionValue("i");
            processFileAndVerify(directoryHasher, persistenceStyle, line, filename, filesToProcess);
        } else if (line.hasOption("o")) {
            final String outputFilename = line.getOptionValue("output");
            final boolean forceOverwrite = line.hasOption("force");

            processFilesAndWrite(directoryHasher, outputFilename, persistenceStyle, forceOverwrite,
                filesToProcess);
        } else {
            LOG.warn("You need either -i or -o");
            System.exit(2);
        }

        LOG.info("Done: {}", (System.currentTimeMillis() - startTime)/1000.0);
        System.exit(0);
    }

    private static PersistenceStyle getPersistenceStyle(final CommandLine line) {
        PersistenceStyle persistenceStyle;
        if (line.hasOption("style")) {
            persistenceStyle = PersistenceStyle.convert(line.getOptionValue("style"));
        } else {
            persistenceStyle = PersistenceStyle.JSON;
        }
        return persistenceStyle;
    }

    private static void processFilesAndWrite(final DirectoryHasher directoryHasher, final String outputFile,
        final PersistenceStyle style, final boolean forceOverwrite, final String[] filesToProcess)
    {
        try {
            final DigestOutputCreator outputCreator = new DigestOutputCreator(System.err, directoryHasher,
                forceOverwrite);

            outputCreator.setOutputFile(outputFile);
            outputCreator.setPersistenceStyle(style);
            outputCreator.generate(filesToProcess);
            outputCreator.finish();
        } catch (final FileNotFoundException ex) {
            System.err.println("File " + outputFile + " exists or not forced to be overwritten. Stop.");
            System.exit(-1);
        }
    }

    private static void processFileAndVerify(final DirectoryHasher directoryHasher,
        final PersistenceStyle persistenceStyle, final CommandLine line, final String filename,
        final String[] filesToProcess)
    {
        try {
            final DirHasherResultVerifier verifier = new DirHasherResultVerifier(directoryHasher,
                persistenceStyle);
            verifier.setIgnoreCase(line.hasOption("ignorecase"));
            verifier.loadDigestsFromFile(filename);
            verifier.generateDigests(filesToProcess);
            verifier.verify(new PrintWriter(System.out, true));
        } catch (final FileNotFoundException ex) {
            LOG.error("A file could not be found.", ex);
            System.exit(-1);
        } catch (final PersistenceException ex) {
            LOG.error("Could not parse the file.", ex);
            System.exit(-EXIT_PERSISTENCE_ERROR);
        }
    }

    private static DirectoryHasher createDirectoryHasher(final CommandLine line) {
        DirectoryHasher directoryHasher = null;
        try {
            directoryHasher = getDirectoryHasherByThreadingModel(line);
            directoryHasher.setVerbose(line.hasOption("verbose"));

            setRequestedAlgorithms(line, directoryHasher);
        } catch (final NoSuchAlgorithmException ex) {
            LOG.error("Algorithm not found", ex);
        } finally {
            try {
                if ((directoryHasher != null) && (directoryHasher.getAlgorithms().size() == 0)) {
                    directoryHasher.addAlgorithm(DEFAULT_ALGORITHM);
                }
            } catch (final NoSuchAlgorithmException ex) {
                LOG.error("Algorithm is not found", ex);
                System.exit(1);
            }
        }

        return directoryHasher;
    }

    private static void setRequestedAlgorithms(final CommandLine line, final DirectoryHasher directoryHasher)
        throws NoSuchAlgorithmException
    {
        if (line.hasOption("all") || line.hasOption("sha-256")) {
            directoryHasher.addAlgorithm("sha-256");
        }
        if (line.hasOption("all") || line.hasOption("sha-1")) {
            directoryHasher.addAlgorithm("sha-1");
        }
        if (line.hasOption("all") || line.hasOption("sha-384")) {
            directoryHasher.addAlgorithm("sha-384");
        }
        if (line.hasOption("all") || line.hasOption("sha-512")) {
            directoryHasher.addAlgorithm("sha-512");
        }
        if (line.hasOption("all") || line.hasOption("md5")) {
            directoryHasher.addAlgorithm("md5");
        }
        if (line.hasOption("all") || line.hasOption("md2")) {
            directoryHasher.addAlgorithm("md2");
        }
    }

    private static DirectoryHasher getDirectoryHasherByThreadingModel(final CommandLine line) {
        DirectoryHasher directoryHasher;
        final ConcurrencyMode concurrencyMode = (line.hasOption("single")) ? ConcurrencyMode.SINGLE
            : ConcurrencyMode.MULTI_THREADING;
        
        directoryHasher = DirectoryHasherCreator.create(concurrencyMode);
        return directoryHasher;
    }

    @SuppressWarnings("static-access")
    private static CommandLine getCommandLine(final String[] theArguments) {
        final CommandLineParser parser = new PosixParser();

        final Options options = new Options();

        options.addOption("h", "help", false, "Get help on the supported commandline options");
        options.addOption("1", "sha-1", false, "Output a sha-1 digest");
        options.addOption("2", "sha-256", false, "Output a sha-256 digest (Default if none given)");
        options.addOption(null, "sha-384", false, "Output a sha-384 digest");
        options.addOption(null, "sha-512", false, "Output a sha-512 digest");
        options.addOption(null, "md5", false, "Output a md5 digest");
        options.addOption(null, "md2", false, "Output a md2 digest (should not be used!)");
        options.addOption("a", "all", false, "Include all available digest algorithms");
        options.addOption("n", "ignorecase", false, "Ignore the case on the file, only used when verifying.");
        options.addOption("v", "verbose", false, "Create verbose output");
        options.addOption("f", "force", false, "Force overwriting any previous output");
        options.addOption(null, "single", false, "Only use single threaded execution path");
        final Option outputOption = OptionBuilder.withLongOpt("output")
            .withDescription("The file the output is written to").hasArg().withArgName("outputfile")
            .create("o");
        options.addOption(outputOption);
        options.addOption(OptionBuilder.withLongOpt("input")
            .withDescription("The file needed to verify the found digests").hasArg().withArgName("inputfile")
            .create("i"));
        options.addOption(OptionBuilder.withLongOpt("style").withDescription("The input/output style to use")
            .hasArg().withArgName("style").create("s"));
        options.addOption(null, "single", false, "Only use single threaded execution path");
        CommandLine line;
        try {
            line = parser.parse(options, theArguments);
            if (line.hasOption("help")) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.setWidth(DEFAULT_TERMINAL_WIDTH);
                formatter.printHelp(USAGE, HEADER, options, FOOTER);
                System.exit(0);
            }
        } catch (final ParseException ex) {
            LOG.error("Failed at parsing the commandline options.", ex);
            final HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("hashtree [options] dir [dir...]", options);
            System.exit(EXIT_PERSISTENCE_ERROR);
            return null;
        }
        return line;
    }
}
