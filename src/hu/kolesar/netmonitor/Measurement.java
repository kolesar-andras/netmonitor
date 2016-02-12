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
    public Integer BSIC;

    public Date date;
    public Location location;

    public String toString() {
        return String.format("%d %d %d %d %d %d %d %d",
            CC, NC, LAC, CID, CH, signal, TA, BSIC);
    }

    public double distance(Measurement other) {
        return location.latlon.greatCircleDistance(other.location.latlon);
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
