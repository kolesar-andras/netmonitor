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
        if (m.location == null) return;
        out.write(String.format(Locale.US, "<node id='90000%08d' visible='true' version='1' lat='%1.7f' lon='%1.7f'>\n",
            ++nodeId, m.location.latlon.lat(), m.location.latlon.lon()));
        writeTag("mcc", m.cell.CC);
        writeTag("mnc", m.cell.NC);
        writeTag("lac", m.cell.LAC);
        writeTag("cellid", m.cell.CID);
        writeTag("ch", m.cell.CH);
        writeTag("bsic", m.cell.BSIC);
        writeTag("rssi", m.signal);
        writeTag("measured", m.date);
        writeTag("net", "gsm");
        writeTag("speed", m.location.speed, 0);
        writeTag("direction", m.location.direction, 0);
        out.write("</node>\n");
        writeCount++;
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

    public void writeTag(String name, double value, int digits) throws IOException {
        writeTag(name, String.format(Locale.US, String.format("%%1.%df", digits), value));
    }
}
