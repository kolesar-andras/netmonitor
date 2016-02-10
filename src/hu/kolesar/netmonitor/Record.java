package hu.kolesar.netmonitor;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.ArrayList;

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

    public Integer getInteger(Pattern pattern, String line) {
        return Integer.valueOf(firstMatch(pattern, line));
    }

    private Integer getCC () {
        return getInteger(patternCC, getPageLine(11, 1));
    }

    private Integer getNC () {
        return getInteger(patternNC, getPageLine(11, 1));
    }

    private Integer getLAC () {
        return getInteger(patternLAC, getPageLine(11, 2));
    }

    private Integer getCID () {
        return getInteger(patternCID, getPageLine(11, 4));
    }

    private Integer getCH () {
        return getInteger(patternCH, getPageLine(11, 3));
    }

    private Integer getSignal () {
        String signalString = getPageLine(1, 1).substring(5, 8).trim();
        if (signalString.equals("xxx")) return null;
        Integer signal = Integer.valueOf(signalString);
        if (signal >= 100) signal = -signal;
        return signal;
    }

    private Integer getTA () {
        String taString = getPageLine(1, 2).substring(3, 5).trim();
        if (taString.equals("xx")) return null;
        return Integer.valueOf(taString);
    }

    public Measurement build() {
        Measurement m = new Measurement();
        m.CC = getCC();
        m.NC = getNC();
        m.LAC = getLAC();
        m.CID = getCID();
        m.CH = getCH();
        m.signal = getSignal();
        m.TA = getTA();
        return m;
    }
}
