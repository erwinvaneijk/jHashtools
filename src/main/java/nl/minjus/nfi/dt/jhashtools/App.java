package nl.minjus.nfi.dt.jhashtools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
public class App 
{
    public static void main( String[] args )
    {
        CommandLine line = App.getCommandLine(args);
        String[] filesToProcess = line.getArgs();

        DirHasher directoryHasher = null;
        if (line.hasOption("sha-256")) {
            directoryHasher = new DirHasher("sha-256");
        }
        if (line.hasOption("sha-1")) {
            if (directoryHasher == null) {
                directoryHasher = new DirHasher("sha-1");
            } else {
                directoryHasher.addAlgorithm("sha-1");
            }
        }
        if (line.hasOption("sha-512")) {
            if (directoryHasher == null) {
                directoryHasher = new DirHasher("sha-512");
            } else {
                directoryHasher.addAlgorithm("sha-512");
            }
        }
        if (line.hasOption("md5")) {
            if (directoryHasher == null) {
                directoryHasher = new DirHasher("md5");
            } else {
                directoryHasher.addAlgorithm("md5");
            }
        }
        if (line.hasOption("ripemd")) {
            if (directoryHasher == null) {
                directoryHasher = new DirHasher("ripemd");
            } else {
                directoryHasher.addAlgorithm("ripemd");
            }
        }
        if (directoryHasher == null) {
            directoryHasher = new DirHasher();
        }

        DirHasherResult digests = new DirHasherResult();
        for (String filename: filesToProcess) {
            System.out.printf("Handling %s\n", filename);
            directoryHasher.updateDigests(digests, new File(filename));
        }

        if (line.hasOption("i") && line.hasOption("o")) {
            Logger.getLogger(App.class.getName()).log(Level.WARNING, "Make up your mind. Cannot do -i and -o at the same time.");
            System.exit(1);
        }
        if (line.hasOption("i")) {
            String filename = line.getOptionValue("i");
            DirHasherResult origDigests = loadDigestsFromFile(filename);
            verifyDigests(System.out, origDigests, digests);
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

    private static DirHasherResult loadDigestsFromFile(String filename) {
        DirHasherResult result = null;
        InputStream stream;
        try {
            File inputFile = new File(filename);
            stream = new FileInputStream(inputFile);
            Persist persist = new JsonPersister();
            result = (DirHasherResult) persist.load(stream, DirHasherResult.class);
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return result;
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
        options.addOption(OptionBuilder.withLongOpt("output")
                .withDescription("The file the output is written to")
                .hasArg()
                .withArgName("outputfile")
                .create("o"));
        options.addOption(OptionBuilder.withLongOpt("input")
                .withDescription("The file needed to verify the found digests")
                .hasArg()
                .withArgName("inputfile")
                .create("i"));
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

    private static void verifyDigests(PrintStream out, DirHasherResult origDigests, DirHasherResult digests) {
        DirHasherResult differences = origDigests.notIntersect(digests);
        if (differences.size() == 0) {
            out.println("There are no differences");
        }
    }
}
