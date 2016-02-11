package hu.kolesar.netmonitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class JsonWriter extends Writer {

    private boolean firstMeasurement;
    private boolean firstTag;

    public JsonWriter(BufferedWriter out) {
        super(out);
    }

    public void start() throws IOException {
        out.write("{\n");
        out.write("  measurements: [\n");
        firstMeasurement = true;
    }

    public void write(Measurement m) throws IOException {
        if (m.latlon == null) return;
        firstTag = true;
        if (firstMeasurement) {
            firstMeasurement = false;
        } else {
            out.write(",\n");
        }
        out.write("    {\n");
        writeTag("lat", m.latlon.lat(), 7);
        writeTag("lon", m.latlon.lon(), 7);
        writeTag("mcc", m.CC);
        writeTag("mnc", m.NC);
        writeTag("lac", m.LAC);
        writeTag("cellid", m.CID);
        writeTag("ch", m.CH);
        writeTag("rssi", m.signal);
        writeTag("measured", m.date);
        writeTag("net", "gsm");
        out.write("\n    }");
    }

    public void end() throws IOException {
        out.write("\n");
        out.write("  ]\n");
        out.write("}\n");
    }

    public void writeRaw(String name, String raw) throws IOException {
        if (name == null) return;
        if (firstTag) {
            firstTag = false;
        } else {
            out.write(",\n");
        }
        out.write(String.format("      %s: %s", name, raw));
    }

    public void writeTag(String name, String value) throws IOException {
        writeRaw(name, String.format("\"%s\"", value));
    }

    public void writeTag(String name, double value, int digits) throws IOException {
        writeRaw(name, String.format(Locale.US, String.format("%%1.%df", digits), value));
    }

    public void writeTag(String name, Integer value) throws IOException {
        writeRaw(name, String.format("%d", value));
    }

    public void writeTag(String name, Date value) throws IOException {
        writeTag(name, String.format("%s", dateFormat.format(value)));
    }
}
