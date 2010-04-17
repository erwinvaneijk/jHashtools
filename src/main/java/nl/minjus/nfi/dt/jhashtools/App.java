package nl.minjus.nfi.dt.jhashtools;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
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

    }

    private static CommandLine getCommandLine(final String[] args) {
        CommandLineParser parser = new PosixParser();

        Options options = new Options();

        options.addOption(null, "sha-256", false, "Output a sha-256 digest");
        options.addOption(null, "sha-1", false, "Output a sha-1 digest");

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
