package hu.kolesar.netmonitor;

public class Filter {

    private final double minDistance = 50; // meters

    Measurement lastMeasurement = null;

    public boolean pass(Measurement measurement) {
        boolean pass = false;
        if (measurement.latlon == null) {
            // ignore
        } else if (lastMeasurement == null) {
            pass = true;
        } else {
            if (measurement.distance(lastMeasurement) > minDistance) pass = true;
            if (measurement.differentCell(lastMeasurement)) pass = true;
        }
        if (pass) lastMeasurement = measurement;
        return pass;
    }
}