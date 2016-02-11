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
}
