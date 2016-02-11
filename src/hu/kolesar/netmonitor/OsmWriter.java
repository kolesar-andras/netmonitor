package hu.kolesar.netmonitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OsmWriter extends Writer {

    int nodeId = 0;

    public OsmWriter(BufferedWriter out) {
        super(out);
    }

    public void start() throws IOException {
        out.write("<?xml version='1.0' encoding='UTF-8'?>\n");
        out.write("<osm version='0.6' upload='false' generator='netmonitor'>\n");
    }

    public void write(Measurement m) throws IOException {
        if (m.latlon == null) return;
        out.write(String.format(Locale.US, "<node id='90000%08d' visible='true' version='1' lat='%1.7f' lon='%1.7f'>\n", ++nodeId, m.latlon.lat(), m.latlon.lon()));
        writeTag("mcc", m.CC);
        writeTag("mnc", m.NC);
        writeTag("lac", m.LAC);
        writeTag("cellid", m.CID);
        writeTag("ch", m.CH);
        writeTag("bsic", m.BSIC);
        writeTag("rssi", m.signal);
        writeTag("measured", m.date);
        writeTag("net", "gsm");
        out.write("</node>\n");
    }

    public void end() throws IOException {
        out.write("</osm>\n");
    }

    public void writeTag(String name, String value) throws IOException {
        if (name == null) return;
        out.write(String.format("  <tag k='%s' v='%s'/>\n", name, value));
        return;
    }

    public void writeTag(String name, Integer value) throws IOException {
        writeTag(name, String.format("%d", value));
    }

    public void writeTag(String name, Date value) throws IOException {
        writeTag(name, String.format("%s", dateFormat.format(value)));
    }

}
