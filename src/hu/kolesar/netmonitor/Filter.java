package hu.kolesar.netmonitor;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

public class Filter {

    private final double minDistance = 50; // meters
    Measurement lastMeasurement = null;
    private Queue queue = null;
    private Writer writer;
    public int parseCount = 0;
    public int locateCount = 0;
    public final HashSet<Cell> unlocatedCells = new HashSet<>();

    public DateInterval measurementInterval = new DateInterval();
    public DateInterval locatedInterval = new DateInterval();

    public Filter(Writer writer) {
        this.writer = writer;
        queue = new Queue();
    }

    public void write(Measurement measurement) {
        parseCount++;
        measurementInterval.add(measurement.date);
        queue.add(measurement);
    }

    public void process(Measurement measurement) throws IOException {
        Measurement next = queue.peekFirst();
        if (measurement.cell.BSIC == null) return;
        // skip last measurement, we can't be sure what would follow
        if (next == null || next.cell.BSIC == null) return;

        if (measurement.location == null) {
            unlocatedCells.add(measurement.cell);
        } else {
            locatedInterval.add(measurement.date);
            locateCount++;
        }
        if (pass(measurement)) writer.write(measurement);
    }

    public void flush() throws IOException {
        queue.flush();
    }

    public boolean pass(Measurement measurement) {
        boolean pass = false;
        if (measurement.location == null) {
            // ignore
        } else if (lastMeasurement == null) {
            pass = true;
        } else {
            if (measurement.distance(lastMeasurement) > minDistance) pass = true;
            if (!measurement.cell.equals(lastMeasurement.cell)) pass = true;
        }
        if (pass) lastMeasurement = measurement;
        return pass;
    }

    class Queue extends LinkedList<Measurement> {

        private static final int limit = 3;

        @Override
        public boolean add(Measurement m) {
            super.add(m);
            try {
                while (size() > limit) purge();
            } catch (IOException e) {
                return false;
            }
            return true;
        }

        public void purge() throws IOException {
            Measurement removed = super.remove();
            process(removed);
        }

        public void flush() throws IOException {
            while (size() > 0) purge();
        }
    }
}
