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
    private long timeOffset;
    private Integer netmonitorPage;
    private Record record;
    private Writer writer;
    private Filter filter;
    private Georeferencer georeferencer;

    public Parser(GpxData gpxData, Writer writer) {
        this.writer = writer;
        record = new Record();
        filter = new Filter();
        georeferencer = new Georeferencer(gpxData);
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
        if (matcher.matches() && phoneTime == null) {
            phoneTime = formatPhoneTime.parse(matcher.group(1));
            setTimeOffset();
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

    private boolean parseFinished() throws IOException {
        if (line.equals("Information: Batch processed, terminating.")) {
            Measurement measurement = record.build();
            measurement.date = getRealTime(systemTime);
            measurement.location = georeferencer.getLocation(measurement.date);
            if (measurement.signal != null && filter.pass(measurement)) {
                writer.write(measurement);
            }
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
        timeOffset = phoneTime.getTime() - systemTime.getTime();
    }

    private Date getRealTime(Date systemTime) {
        return new Date(systemTime.getTime() + timeOffset);
    }
}
