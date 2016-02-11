package hu.kolesar.netmonitor;

import java.io.IOException;

public abstract class Writer {

    public abstract void start() throws IOException;
    public abstract void write(Measurement measurement) throws IOException;
    public abstract void end() throws IOException;

}
