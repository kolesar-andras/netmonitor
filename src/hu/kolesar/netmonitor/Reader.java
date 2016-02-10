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

    public static void main(String[] args) throws IOException, ParseException, SAXException {
        if (args.length < 1) {
            System.err.println("Please specify trackfile name.\n");
            return;
        }
        loadTrack(args[0]);
        loadStdin();
    }

    private static void loadTrack(String filename) throws IOException, SAXException {
        GpxData data = null;
        try (InputStream iStream = new FileInputStream(new File(filename))) {
            GpxReader reader = new GpxReader(iStream);
            reader.parse(false);
            data = reader.getGpxData();
        }
    }

    private static void loadStdin() throws IOException, ParseException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
        Parser parser = new Parser(out);
        String line;
        while ((line = in.readLine()) != null) {
            parser.parseLine(line);
        }
        System.out.printf("lines: %d\n", parser.getLineCount());
    }
}
