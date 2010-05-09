/*
 * Copyright (c) 2010. Erwin van Eijk <erwin.vaneijk@gmail.com>
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
 */

package nl.minjus.nfi.dt.jhashtools;

import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import nl.minjus.nfi.dt.jhashtools.persistence.JsonPersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceProvider;
import nl.minjus.nfi.dt.jhashtools.persistence.PersistenceStyle;
import nl.minjus.nfi.dt.jhashtools.utils.Version;
import org.apache.commons.cli.*;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

import static java.util.logging.Logger.getLogger;

/**
 * Hello world!
 *
 */
public class App {

    private static final String USAGE = "[options] dir [dir...]";
    private static final String HEADER = "hashtree - Creating a list of digests for files and/or directories.\nCopyright (c) 2010, Erwin van Eijk";
    private static final String FOOTER = "";
    
    public static void main(String[] arguments) {
        CommandLine line = App.getCommandLine(arguments);
        String[] filesToProcess = line.getArgs();

        DirHasher directoryHasher = createDirectoryHasher(line);

        getLogger(App.class.getName()).log(Level.INFO, "Version: " + Version.getVersion());
        
        DirHasherResult digests = new DirHasherResult();
        for (String filename : filesToProcess) {
            getLogger(App.class.getName()).log(Level.INFO, "Handling directory or file " + filename);
            directoryHasher.updateDigests(digests, new File(filename));
        }

        if (line.hasOption("i") && line.hasOption("o")) {
            getLogger(App.class.getName()).log(Level.WARNING, "Make up your mind. Cannot do -i and -o at the same time.");
            System.exit(1);
        }

        if (line.hasOption("i")) {
            String filename = line.getOptionValue("i");

            PersistenceStyle persistenceStyle = null;
            if (line.hasOption("style")) {
                persistenceStyle = PersistenceStyle.convert(line.getOptionValue("style"));
            } else {
                persistenceStyle = PersistenceStyle.JSON;
            }

            verifyFoundDigests(digests, filename, persistenceStyle, line.hasOption("ignorecase"));
        } else if (line.hasOption("o")) {
            DirHasherResult resultFileDigests = persistDigestsToFile(digests, line.getOptionValue("output"), line.hasOption("force"));
            outputDigests(System.err, resultFileDigests);
        }

        System.exit(0);
    }

    private static void outputDigests(PrintStream out, DirHasherResult resultFileDigests) {
        out.printf("Generated with hashtree (java) by %s\n", System.getProperty("user.name"));
        resultFileDigests.prettyPrint(out);
    }

    private static void verifyFoundDigests(DirHasherResult digests, String filename, PersistenceStyle parseNewStyle, boolean ignoreCase) {
        DirHasherResultVerifier verifier = new DirHasherResultVerifier(digests, parseNewStyle);
        verifier.setIgnoreCase(ignoreCase);
        verifier.loadDigestsFromFile(filename);
        verifier.verify(System.out);
    }

    private static DirHasher createDirectoryHasher(CommandLine line) {
        DirHasher directoryHasher = null;
        try {
            directoryHasher = new DirHasher(FileHasher.NO_ALGORITHM);

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
        } catch (NoSuchAlgorithmException ex) {
            getLogger(App.class.getName()).log(Level.SEVERE, "Algorithm not found", ex);
        } finally {
            try {
                if ((directoryHasher != null) && (directoryHasher.getAlgorithms().size() == 0)) {
                    directoryHasher.addAlgorithm(FileHasher.DEFAULT_ALGORITHM);
                }
            } catch (NoSuchAlgorithmException ex) {
                getLogger(App.class.getName()).log(Level.SEVERE, "Algorithm is not found", ex);
                System.exit(1);
            }
        }

        if ((directoryHasher != null) && line.hasOption("verbose")) {
            directoryHasher.setVerbose(true);
        }
        return directoryHasher;
    }

    private static DirHasherResult persistDigestsToFile(DirHasherResult digests, String outputFilename, boolean force) {
        OutputStream file = null;
        try {
            getLogger(App.class.getName()).log(Level.INFO, "Writing the results to " + outputFilename);
            File outputFile = new File(outputFilename);
            if (outputFile.exists() && !force) {
                getLogger(App.class.getName()).log(Level.SEVERE, "Output file exists. Aborting");
                System.exit(-1);
            }
            file = new FileOutputStream(outputFile);
            PersistenceProvider persistenceProvider = new JsonPersistenceProvider();
            persistenceProvider.persist(file, digests);
            file.flush();
            
            DirHasher d = new DirHasher(digests.firstEntry().getValue().getAlgorithms());
            return d.getDigests(outputFile);
        } catch (PersistenceException ex) {
            getLogger(App.class.getName()).log(Level.SEVERE, "Cannot persist content to file", ex);
        } catch (IOException ex) {
            getLogger(App.class.getName()).log(Level.SEVERE, "Cannot create file", ex);
        } catch (NoSuchAlgorithmException ex) {
            getLogger(App.class.getName()).log(Level.SEVERE, "Cannot create the algorithm", ex);
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException ex) {
                getLogger(App.class.getName()).log(Level.SEVERE, "Cannot close file", ex);
            }
        }
        return null;
    }

    @SuppressWarnings("static-access")
    private static CommandLine getCommandLine(final String[] args) {
        CommandLineParser parser = new PosixParser();

        Options options = new Options();

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
        Option outputOption =
                OptionBuilder.withLongOpt("output")
                    .withDescription("The file the output is written to")
                        .hasArg()
                        .withArgName("outputfile")
                        .create("o");
        options.addOption(outputOption);
        options.addOption(OptionBuilder.withLongOpt("input").withDescription("The file needed to verify the found digests").hasArg().withArgName("inputfile").create("i"));
        options.addOption(OptionBuilder.withLongOpt("style").withDescription("The input/output style to use").hasArg().withArgName("style").create("s"));
        CommandLine line;
        try {
            line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.setWidth(80);
                formatter.printHelp(USAGE, HEADER, options, FOOTER);
                System.exit(0);
            }
        } catch (ParseException ex) {
            getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("hashtree [options] dir [dir...]", options);
            return null;
        }
        return line;
    }
}
