package hu.kolesar.netmonitor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public abstract class Writer {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    BufferedWriter out;

    public Writer(BufferedWriter out) {
        this.out = out;
    }

    public abstract void start() throws IOException;
    public abstract void write(Measurement measurement) throws IOException;
    public abstract void end() throws IOException;

}
