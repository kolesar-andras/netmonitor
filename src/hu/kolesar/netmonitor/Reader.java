package hu.kolesar.netmonitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;

import org.openstreetmap.josm.io.GpxReader;
import org.openstreetmap.josm.data.gpx.GpxData;
import org.xml.sax.SAXException;

public class Reader {

    private static GpxData gpxData;
    private static Writer writer;

    public static void main(String[] args) throws IOException, ParseException, SAXException, org.apache.commons.cli.ParseException {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
        Cli cli = new Cli(args);

        if (cli.cmd.hasOption("format") && cli.cmd.getOptionValue("format").equals("osm")) {
            writer = new OsmWriter(out);
        } else {
            writer = new JsonWriter(out);
        }
        writer.start();

        loadTrack(cli.cmd.getArgs()[0]);
        loadInput(in);

        in.close();
        writer.end();
        out.flush();

    }

    private static void loadTrack(String filename) throws IOException, SAXException {
        try (InputStream iStream = new FileInputStream(new File(filename))) {
            GpxReader reader = new GpxReader(iStream);
            reader.parse(false);
            gpxData = reader.getGpxData();
        }
    }

    private static void loadInput(BufferedReader in) throws IOException, ParseException {
        Parser parser = new Parser(gpxData, writer);
        String line;
        while ((line = in.readLine()) != null) {
            parser.parseLine(line);
        }
        if (verbose()) parser.printStats();
    }

    public static boolean verbose() {
        return Cli.instance.cmd.hasOption("verbose");
    }
}
