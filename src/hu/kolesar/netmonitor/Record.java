package hu.kolesar.netmonitor;

import java.lang.StringBuilder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.ParseException;

public class Record {

    private static final Pattern patternCC  = Pattern.compile("CC: *([0-9]+)");
    private static final Pattern patternNC  = Pattern.compile("NC *([0-9]+)");
    private static final Pattern patternLAC = Pattern.compile("LAC: *([0-9]+)");
    private static final Pattern patternCID = Pattern.compile("CID: *([0-9]+)");
    private static final Pattern patternCH  = Pattern.compile("CH *: *([0-9]+)");

    private HashMap<Integer, ArrayList<String>> pages;
    private int row = 0;

    public Record() {
        pages = new HashMap<>();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<Integer,ArrayList<String>> entry : pages.entrySet()) {
            builder.append(String.format("#%d\n", entry.getKey()));
            for (String row : entry.getValue()) {
                builder.append(row + "\n");
            }
        }
        return builder.toString();
    }

    public void addRow(Integer netmonitorPage, String line) {
        if (!pages.containsKey(netmonitorPage))
            pages.put(netmonitorPage, new ArrayList<String>());
        ArrayList<String> page = pages.get(netmonitorPage);
        page.add(line);
    }

    public String getPageLine(int page, int line) {
        return pages.get(page).get(line-1);
    }

    public String firstMatch(Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) return null;
        return matcher.group(1);
    }

    private Integer getCC() {
        return getInteger(patternCC, getPageLine(11, 1));
    }

    private Integer getNC() {
        return getInteger(patternNC, getPageLine(11, 1));
    }

    private Integer getLAC() {
        return getInteger(patternLAC, getPageLine(11, 2));
    }

    private Integer getCID() {
        return getInteger(patternCID, getPageLine(11, 4));
    }

    private Integer getCH() {
        return getInteger(patternCH, getPageLine(11, 3));
    }

    private Integer getSignal() {
        String cut = getPageLine(1, 1).substring(5, 8);
        if (cut.substring(0, 2).equals("- ")) return null; // Nokia bug
        Integer signal = toInteger(cut);
        if (signal != null && signal >= 100) signal = -signal;
        return signal;
    }

    private Integer getTA() {
        return toInteger(getPageLine(1, 2).substring(3, 5));
    }

    private Integer getBSIC() {
        return toInteger(getPageLine(2, 1).substring(10, 12));
    }

    public Integer getInteger(Pattern pattern, String line) {
        String match = firstMatch(pattern, line);
        return toInteger(match);
    }

    private Integer toInteger(String string) {
        if (string == null) return null;
        string = string.trim();
        if (string.substring(0, 1).equals("x")) return null;
        return Integer.valueOf(string);
    }

    public Measurement build() {
        try {
            Cell c = new Cell();
            c.CC = getCC();
            c.NC = getNC();
            c.LAC = getLAC();
            c.CID = getCID();
            c.CH = getCH();
            c.BSIC = getBSIC();

            Measurement m = new Measurement(c);
            m.signal = getSignal();
            m.TA = getTA();
            return m;

        } catch (NumberFormatException e) {
            StringBuilder builder = new StringBuilder();
            builder.append("Parse error: " + e.getMessage() + "\n");
            builder.append(this.toString());
            throw new NumberFormatException(builder.toString());
        }
    }
}
