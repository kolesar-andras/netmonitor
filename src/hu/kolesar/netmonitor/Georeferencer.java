package hu.kolesar.netmonitor;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.data.coor.LatLon;

import java.util.Date;
import java.util.Iterator;

public class Georeferencer {

    private GpxData gpxData;

    private Iterator<GpxTrack> itGpxTrack;
    private Iterator<GpxTrackSegment> itGpxTrackSegment;
    private Iterator<WayPoint> itWayPoint;

    private Pair lastPair = null;
    private WayPoint lastWayPoint = null;
    private boolean noMoreWayPoint = false;

    public Georeferencer(GpxData gpxData) {
        this.gpxData = gpxData;
    }

    public LatLon getLatLon(Date date) {
        try {
            Pair pair = findPair(date);
            return pair.before.getCoor();
        } catch (OutOfTrackException e) {
            return null;
        }
    }

    public Pair findPair(Date date) throws OutOfTrackException {
        double dateAsDouble = dateAsDouble(date);
        if (lastPair != null && lastPair.isWithin(dateAsDouble)) return lastPair;
        WayPoint pnt;
        while (null != (pnt = nextWayPoint())) {
            if (pnt.time > dateAsDouble) {
                if (lastWayPoint == null) {
                    break;
                } else {
                    lastPair = new Pair(lastWayPoint, pnt);
                    lastWayPoint = pnt;
                    return lastPair;
                }
            }
            lastWayPoint = pnt;
        }
        noMoreWayPoint = true;
        throw new OutOfTrackException();
    }

    public GpxTrack nextGpxTrack() {
        if (itGpxTrack == null) itGpxTrack = gpxData.tracks.iterator();
        if (!itGpxTrack.hasNext()) return null;
        return itGpxTrack.next();
    }

    public GpxTrackSegment nextGpxTrackSegment() {
        while (itGpxTrackSegment == null || !itGpxTrackSegment.hasNext()) {
            GpxTrack gpxTrack = nextGpxTrack();
            if (gpxTrack == null) return null;
            itGpxTrackSegment = gpxTrack.getSegments().iterator();
        }
        return itGpxTrackSegment.next();
    }

    public WayPoint nextWayPoint() {
        while (itWayPoint == null || !itWayPoint.hasNext()) {
            GpxTrackSegment gpxTrackSegment = nextGpxTrackSegment();
            if (gpxTrackSegment == null) return null;
            itWayPoint = gpxTrackSegment.getWayPoints().iterator();
        }
        return itWayPoint.next();
    }

    public static LatLon interpolate(Date date, Pair pair) {
        double proportion = (dateAsDouble(date) - pair.before.time) / (pair.after.time - pair.before.time);
        return pair.before.getCoor().interpolate(pair.after.getCoor(), proportion);
    }

    public static double dateAsDouble(Date date) {
        return (double) (date.getTime()/1000);
    }

    class Pair {
        public WayPoint before;
        public WayPoint after;

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
    }

    class OutOfTrackException extends Exception {}
}
