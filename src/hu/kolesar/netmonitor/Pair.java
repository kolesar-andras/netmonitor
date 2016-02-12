package hu.kolesar.netmonitor;

import java.util.Date;

import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.data.coor.LatLon;

public class Pair {
    public final WayPoint before;
    public final WayPoint after;
    private Float length;

    public Pair(WayPoint before, WayPoint after) {
        this.before = before;
        this.after = after;
    }

    public String toString() {
        return before.toString() + '-' + after.toString();
    }

    public boolean isWithin(double dateAsDouble) {
        return dateAsDouble >= before.time && dateAsDouble <= after.time;
    }

    public LatLon interpolate(Date date) {
        double proportion = (dateAsDouble(date) - before.time) / getElapsedTime();
        return before.getCoor().interpolate(after.getCoor(), proportion);
    }

    public Location getLocation(Date date) {
        Location location = new Location(interpolate(date));
        location.speed = new Float(getSpeed() * 3.6); // kilometers / hour
        location.direction = new Float(getHeading() * 180 / Math.PI); // degrees
        return location;
    }

    public double getHeading() {
        return before.getCoor().heading(after.getCoor()); // radians
    }

    public float getSpeed() {
        return getLength() / (float) getElapsedTime(); // meters / second
    }

    public float getLength() {
        if (length == null) length = new Float(before.getCoor().greatCircleDistance(after.getCoor()));
        return length.floatValue();
    }

    public double getElapsedTime() {
        return after.time - before.time;
    }

    public static double dateAsDouble(Date date) {
        return (double) (date.getTime()/1000);
    }
}
