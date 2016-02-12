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

    public DateInterval waypointInterval = new DateInterval();

    public Georeferencer(GpxData gpxData) {
        this.gpxData = gpxData;
    }

    public Location getLocation(Date date) {
        try {
            Pair pair = findPair(date);
            return pair.getLocation(date);
        } catch (OutOfTrackException e) {
            return null;
        }
    }

    public Pair findPair(Date date) throws OutOfTrackException {
        double dateAsDouble = Pair.dateAsDouble(date);
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
        WayPoint waypoint = itWayPoint.next();
        waypointInterval.add(waypoint.getTime());
        return waypoint;
    }

    public void flush() {
        while (null != nextWayPoint());
    }

    class OutOfTrackException extends Exception {}
}
