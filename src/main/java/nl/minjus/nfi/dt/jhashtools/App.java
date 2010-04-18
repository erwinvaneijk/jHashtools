package nl.minjus.nfi.dt.jhashtools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        DirHasher hasher = null;
        if (line.hasOption("sha-256")) {
            hasher = new DirHasher("sha-256");
        }
        if (line.hasOption("sha-1")) {
            if (hasher == null) {
                hasher = new DirHasher("sha-1");
            } else {
                hasher.addAlgorithm("sha-1");
            }
        }
        if (hasher == null) {
            hasher = new DirHasher();
        }

        // @fixme
        // generate a test on this one.
        Map<String, DigestsResults> digests = new TreeMap<String, DigestsResults>();
        for (String filename: filesToProcess) {
            System.out.printf("Handling file %s\n", filename);
            hasher.updateDigests(digests, new File(filename));
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Type digestType = new TypeToken<Digest>() {}.getType();
        gsonBuilder.registerTypeAdapter(digestType, new DigestSerializer());

        Gson gson = gsonBuilder.create();
        String result = gson.toJson(digests);
        System.out.println(result);

        Type fullDigestList = new TypeToken<Map<String, DigestsResults>>() {}.getType();
        digests = gson.fromJson(result, fullDigestList);

        System.out.println(digests.toString());
    }

    @SuppressWarnings("static-access")
    private static CommandLine getCommandLine(final String[] args) {
        CommandLineParser parser = new PosixParser();

        Options options = new Options();

        options.addOption("2", "sha-256", false, "Output a sha-256 digest");
        options.addOption("1", "sha-1", false, "Output a sha-1 digest");
        options.addOption(OptionBuilder.withLongOpt("output")
                .withDescription("The file the output is written to")
                .hasArg()
                .withArgName("outputfile")
                .create("o"));
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("hashtree", options);
            return null;
        }
        return line;
    }
}
