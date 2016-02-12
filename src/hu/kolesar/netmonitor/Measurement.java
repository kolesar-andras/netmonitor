package hu.kolesar.netmonitor;

import java.util.Date;
import org.openstreetmap.josm.data.coor.LatLon;

public class Measurement {

    public Cell cell;

    public Integer signal;
    public Integer TA;

    public Date date;
    public Location location;

    public Measurement(Cell cell) {
        this.cell = cell;
    }

    public String toString() {
        return String.format("%s %d %d",
            cell, signal, TA);
    }

    public double distance(Measurement other) {
        return location.latlon.greatCircleDistance(other.location.latlon);
    }
}
