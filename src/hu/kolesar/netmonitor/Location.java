package hu.kolesar.netmonitor;

import org.openstreetmap.josm.data.coor.LatLon;

public class Location {

    public LatLon latlon;
    public Float speed;
    public Float direction;

    public Location(LatLon latlon) {
        this.latlon = latlon;
    }
}
