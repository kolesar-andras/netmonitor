package hu.kolesar.netmonitor;

import org.openstreetmap.josm.data.gpx.GpxData;
import org.openstreetmap.josm.data.gpx.GpxTrack;
import org.openstreetmap.josm.data.gpx.GpxTrackSegment;
import org.openstreetmap.josm.data.gpx.WayPoint;
import org.openstreetmap.josm.data.coor.LatLon;

import java.util.Date;

public class Georeferencer {

    private GpxData gpxData;

    public Georeferencer(GpxData gpxData) {
        this.gpxData = gpxData;
    }

    public LatLon getLatLon(Measurement measurement, Date date) {
        try {
            Pair pair = findPair(date);
            return pair.before.getCoor();
        } catch (OutOfTrackException e) {
            return null;
        }
    }

    public Pair findPair(Date date) throws OutOfTrackException {
        double dateAsDouble = (double) (date.getTime()/1000);
        WayPoint lastWayPoint = null;
        for (GpxTrack trk: gpxData.tracks) {
            for (GpxTrackSegment seg : trk.getSegments()) {
                lastWayPoint = null;
                for (WayPoint pnt : seg.getWayPoints()) {
                    if (pnt.time > dateAsDouble) {
                        if (lastWayPoint == null) {
                            break;
                        } else {
                            return new Pair(lastWayPoint, pnt);
                        }
                    }
                    lastWayPoint = pnt;
                }
            }
        }
        throw new OutOfTrackException();
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
    }

    class OutOfTrackException extends Exception {}
}
