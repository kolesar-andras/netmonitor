package hu.kolesar.netmonitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.coor.LatLon;

public class Parser {

    private static final Pattern patternSystemTime = Pattern.compile( "\\[([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})\\]");
    private static final Pattern patternPhoneTime = Pattern.compile("Phone time is (.+)");
    private static final Pattern patternNetmonitorPage = Pattern.compile("nokianetmonitor ([0-9]{2})");

    private DateFormat formatSystemTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat formatPhoneTime = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private String line;
    private int lineCount = 0;
    private Date systemTime;
    private Date phoneTime;
    private long phoneTimeOffset = 0; // phone time - real time
    private long systemTimeOffset; // system time - real time
    private Integer netmonitorPage;
    private Record record;
    private Writer writer;
    private Filter filter;
    private Georeferencer georeferencer;

    public Parser(GpxData gpxData, Writer writer) {
        this.writer = writer;
        record = new Record();
        filter = new Filter(writer);
        georeferencer = new Georeferencer(gpxData);
        if (Cli.instance.cmd.hasOption("offset")) {
            phoneTimeOffset = Integer.parseInt(Cli.instance.cmd.getOptionValue("offset")) * 1000;
            if (Reader.verbose())
                System.err.printf("phone time offset: %d s\n", phoneTimeOffset/1000);
        }
    }

    public boolean parseLine(String line) throws IOException, ParseException {
        this.line = line;
        lineCount++;
        return
            parseIgnoredLines() ||
            parseSystemTime() ||
            parsePhoneTime() ||
            parseNetmonitorPage() ||
            parseFinished() ||
            parseNetmonitorRow();
    }

    public int getLineCount() {
        return lineCount;
    }

    private boolean parseIgnoredLines() {
        return
            line.equals("-- startup") ||
            line.equals("Frame not requested right now. See <http://wammu.eu/support/bugs/> for information how to report it.") ||
            line.equals("No response in specified timeout. Probably phone not connected.");
    }

    private boolean parseSystemTime() throws ParseException {
        Matcher matcher = patternSystemTime.matcher(line);
        if (matcher.matches()) {
            systemTime = formatSystemTime.parse(matcher.group(1));
            return true;
        }
        return false;
    }

    private boolean parsePhoneTime() throws ParseException {
        Matcher matcher = patternPhoneTime.matcher(line.trim());
        if (matcher.matches()) {
            if (phoneTime == null) {
                phoneTime = formatPhoneTime.parse(matcher.group(1));
                setTimeOffset();
            }
            return true;
        }
        return false;
    }

    private boolean parseNetmonitorPage() {
        Matcher matcher = patternNetmonitorPage.matcher(line);
        if (matcher.matches()) {
            netmonitorPage = Integer.valueOf(matcher.group(1));
            return true;
        }
        return false;
    }

    private boolean parseFinished() throws IOException, NumberFormatException {
        if (line.equals("Information: Batch processed, terminating.")) {
            Measurement measurement = record.build();
            measurement.date = getRealTime(systemTime);
            measurement.location = georeferencer.getLocation(measurement.date);
            filter.write(measurement);

            record = new Record();
            return true;
        }
        return false;
    }

    private boolean parseNetmonitorRow() {
        record.addRow(netmonitorPage, line);
        return true;
    }

    private void setTimeOffset() {
        if (systemTime == null) return;
        systemTimeOffset = systemTime.getTime() - (phoneTime.getTime() - phoneTimeOffset);
        if (Reader.verbose())
            System.err.printf("system time offset: %d s\n", systemTimeOffset/1000);
    }

    private Date getRealTime(Date systemTime) {
        return new Date(systemTime.getTime() - systemTimeOffset);
    }

    public void printStats() {
        System.err.printf("input line count: %d\n", getLineCount());
        System.err.printf("parsed:  %d\n", filter.parseCount);
        System.err.printf("located: %d\n", filter.locateCount);
        System.err.printf("written: %d\n", writer.writeCount);
        System.err.printf("unlocated unique cells: %d\n", filter.unlocatedCells.size());
        // for (Cell cell : filter.unlocatedCells) System.err.println(cell);
        System.err.printf("measurements: %s\n", filter.measurementInterval);
        System.err.printf("trackpoints:  %s\n", georeferencer.waypointInterval);
        System.err.printf("located:      %s\n", filter.locatedInterval);
    }

    public void flush() throws IOException {
        filter.flush();
        georeferencer.flush();
    }
}
