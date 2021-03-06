package hu.kolesar.netmonitor;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class Cli {

    public static Cli instance;
    public CommandLine cmd;

    public Cli(String[] args) throws ParseException {
        instance = this;
        Options options = new Options();
        options.addOption("h", "help", false, "show help");
        options.addOption("v", "verbose", false, "verbose output");

        options.addOption(Option.builder("f")
            .longOpt("format")
            .argName("file format")
            .desc("output file format [json|osm]")
            .hasArg()
            .build()
        );

        options.addOption(Option.builder("o")
            .longOpt("offset")
            .argName("seconds")
            .desc("offset = phone time - time used in trackfile (in local timezone)")
            .hasArg()
            .build()
        );

        options.addOption(Option.builder()
            .longOpt("print-system-offset")
            .desc("print system offset and exit")
            .build()
        );

        CommandLineParser parser = new BasicParser();
        cmd = parser.parse(options, args);

        if (!cmd.hasOption("print-system-offset") &&
            (cmd.getArgs().length == 0 || cmd.hasOption("help"))) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("[options] <trackfile>", options);
            System.exit(1);
        }
    }
}
