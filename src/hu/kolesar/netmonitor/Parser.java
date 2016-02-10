package hu.kolesar.netmonitor;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Parser {

    private static final Pattern patternSystemTime = Pattern.compile( "\\[([0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2})\\]");
    private static final Pattern patternPhoneTime = Pattern.compile("Phone time is (.+)");
    private static final Pattern patternNetmonitorPage = Pattern.compile("nokianetmonitor ([0-9]{2})");

    private String line;
    private int lineCount = 0;
    private String systemTime;
    private String phoneTime;
    private Integer netmonitorPage;
    private Record record;

    public Parser() {
        record = new Record();
    }

    public boolean parseLine(String line) {
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

    private boolean parseSystemTime() {
        if (patternSystemTime.matcher(line).matches()) {
            systemTime = line;
            return true;
        }
        return false;
    }

    private boolean parsePhoneTime() {
        if (patternPhoneTime.matcher(line).matches()) {
            phoneTime = line;
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

    private boolean parseFinished() {
        if (line.equals("Information: Batch processed, terminating.")) {
            record.build();
            record = new Record();
            return true;
        }
        return false;
    }

    private boolean parseNetmonitorRow() {
        record.addRow(netmonitorPage, line);
        return true;
    }
}
