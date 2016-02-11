package hu.kolesar.netmonitor;

import java.util.Date;
import org.openstreetmap.josm.data.coor.LatLon;

public class Measurement {

    public Integer CC;
    public Integer NC;
    public Integer LAC;
    public Integer CID;
    public Integer CH;
    public Integer signal;
    public Integer TA;

    public Date date;
    public LatLon latlon;

    public String toString() {
        return String.format("%d %d %d %d %d %d %d",
            CC, NC, LAC, CID, CH, signal, TA);
    }

    public double distance(Measurement other) {
        return latlon.greatCircleDistance(other.latlon);
    }

    public boolean differentCell(Measurement other) {
        return !(
            CC != null && CC.equals(other.CC) &&
            NC != null && NC.equals(other.NC) &&
            LAC != null && LAC.equals(other.LAC) &&
            CID != null && CID.equals(other.CID)
        );
    }
}
