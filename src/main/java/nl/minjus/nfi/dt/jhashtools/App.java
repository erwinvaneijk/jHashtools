package nl.minjus.nfi.dt.jhashtools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.minjus.nfi.dt.jhashtools.persistence.JsonPersister;
import nl.minjus.nfi.dt.jhashtools.persistence.Persist;
import nl.minjus.nfi.dt.jhashtools.exceptions.PersistenceException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        CommandLine line = App.getCommandLine(args);
        String[] filesToProcess = line.getArgs();

        DirHasher directoryHasher = null;
        try {
            directoryHasher = new DirHasher(FileHasher.NO_ALGORITHM);
            if (line.hasOption("sha-256")) {
                directoryHasher.addAlgorithm("sha-256");
            }
            if (line.hasOption("sha-1")) {
                directoryHasher.addAlgorithm("sha-1");
            }
            if (line.hasOption("sha-512")) {
                directoryHasher.addAlgorithm("sha-512");
            }
            if (line.hasOption("md5")) {
                directoryHasher.addAlgorithm("md5");
            }
            if (line.hasOption("ripemd")) {
                directoryHasher.addAlgorithm("ripemd");
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Algoritm not found", ex);
        } finally {
            directoryHasher.addAlgorithm(FileHasher.DEFAULT_ALGORITHM);
        }

        if (line.hasOption("verbose")) {
            directoryHasher.setVerbose(true);
        }

        DirHasherResult digests = new DirHasherResult();
        for (String filename : filesToProcess) {
            Logger.getLogger(App.class.getName()).log(Level.INFO, "Handling directory or file " + filename);
            directoryHasher.updateDigests(digests, new File(filename));
        }

        if (line.hasOption("i") && line.hasOption("o")) {
            Logger.getLogger(App.class.getName()).log(Level.WARNING, "Make up your mind. Cannot do -i and -o at the same time.");
            System.exit(1);
        }
        if (line.hasOption("i")) {
            String filename = line.getOptionValue("i");
            DirHasherResultVerifier verifier = new DirHasherResultVerifier(digests);
            verifier.loadDigestsFromFile(filename);
            verifier.verify(System.out);
        } else if (line.hasOption("o")) {
            persistDigestsToFile(digests, line.getOptionValue("output"));
        }

        System.exit(0);
    }

    private static void persistDigestsToFile(DirHasherResult digests, String outputFilename) {
        OutputStream file = null;
        try {
            File outputFile = new File(outputFilename);
            if (outputFile.exists()) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Output file exists. Aborting");
                System.exit(-1);
            }
            file = new FileOutputStream(outputFile);
            Persist persist = new JsonPersister();
            persist.persist(file, digests);
        } catch (PersistenceException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Cannot persist content to file", ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Cannot create file", ex);
        } finally {
            try {
                file.close();
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Cannot close file", ex);
            }
        }
    }

    @SuppressWarnings("static-access")
    private static CommandLine getCommandLine(final String[] args) {
        CommandLineParser parser = new PosixParser();

        Options options = new Options();

        options.addOption("h", "help", false, "Get help on the supported commandline options");
        options.addOption("2", "sha-256", false, "Output a sha-256 digest (Default if none given)");
        options.addOption("1", "sha-1", false, "Output a sha-1 digest");
        options.addOption(null, "sha-512", false, "Output a sha-512 digest");
        options.addOption(null, "md5", false, "Output a md5 digest");
        options.addOption(null, "ripemd", false, "Output a ripemd digest");
        options.addOption("v", "verbose", false, "Create verbose output");
        options.addOption(OptionBuilder.withLongOpt("output").withDescription("The file the output is written to").hasArg().withArgName("outputfile").create("o"));
        options.addOption(OptionBuilder.withLongOpt("input").withDescription("The file needed to verify the found digests").hasArg().withArgName("inputfile").create("i"));
        CommandLine line;
        try {
            line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("hashtree [options] dir [dir...]", options);
                System.exit(0);
            }
        } catch (ParseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("hashtree [options] dir [dir...]", options);
            return null;
        }
        return line;
    }
}
